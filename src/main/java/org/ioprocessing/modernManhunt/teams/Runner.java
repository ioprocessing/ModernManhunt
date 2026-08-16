package org.ioprocessing.modernManhunt.teams;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;
import org.ioprocessing.modernManhunt.gamestate.ManhuntState;
import org.ioprocessing.modernManhunt.items.CustomItems;
import org.ioprocessing.modernManhunt.menus.UI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Runner implements Listener {
    private final ConfigManager<CoreConfig> coreConfig;

    public Runner(Core plugin) {
        this.coreConfig = plugin.getCoreConfig();
    }

    private final Map<UUID, Long> lastWarn = new HashMap<>(); // Players who have received a warning message recently
    private static final long MESSAGE_COOLDOWN = 1000;

    public static final NamespacedKey DEAD_RUNNER = new NamespacedKey(Core.getInstance(), "DeadRunner");
    public static final NamespacedKey SCOUTING = new NamespacedKey(Core.getInstance(), "Scouting");

    public static final NamespacedKey CONFIRM_START_KEY = new NamespacedKey(Core.getInstance(), "ConfirmStart");

    /**
     * @return Confirmation item that sets the spawn location of the world and begins the manhunt when clicked.
     */
    public static ItemStack getConfirmStart() {
        ItemStack confirmStart = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = confirmStart.getItemMeta();

        // Custom item properties
        TextComponent confirmStartName = Component.text("Confirm Start").decoration(TextDecoration.ITALIC, false);

        String miniMsg = "<!i><white>USE <gray>to confirm start location";
        Component message = MiniMessage.miniMessage().deserialize(miniMsg);

        meta.lore(List.of(message));

        meta.getPersistentDataContainer().set(CONFIRM_START_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        confirmStart.setItemMeta(meta);
        confirmStart.setData(DataComponentTypes.ITEM_NAME, confirmStartName);

        return confirmStart;
    }

    public static Boolean isDead(Player player) {
        if(player.getPersistentDataContainer().has(DEAD_RUNNER, PersistentDataType.BOOLEAN))
            return player.getPersistentDataContainer().get(DEAD_RUNNER, PersistentDataType.BOOLEAN);
        return false;
    }

    public static Boolean isScouting(Player player) {
        if(player.getPersistentDataContainer().has(SCOUTING, PersistentDataType.BOOLEAN))
            return player.getPersistentDataContainer().get(SCOUTING, PersistentDataType.BOOLEAN);
        return false;
    }

    public static void setDead(Player player, boolean value) {
        player.getPersistentDataContainer().set(DEAD_RUNNER, PersistentDataType.BOOLEAN, value);
    }

    public static void setScouting(Player player, boolean value) {
        player.getPersistentDataContainer().set(SCOUTING, PersistentDataType.BOOLEAN, value);
    }

    public static void clearDead(Player player) {
        player.getPersistentDataContainer().remove(DEAD_RUNNER);
    }

    public static void clearScouting(Player player) {
        player.getPersistentDataContainer().remove(SCOUTING);
    }

    /**
     * Stop scouters from interacting with the world.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // If the player is a runner that left mid-scout, re-apply flight
        if (Runner.isScouting(player)) {
            if(Core.getInstance().getManhuntManager().getState() == ManhuntState.SCOUTING)
                Core.getInstance().getManhuntManager().startPlayerAsScout(player);
            else Core.getInstance().getManhuntManager().resetPlayer(player);
        }
    }

    @EventHandler
    public void onScoutDamage(EntityDamageEvent event) {
        // Cancel all damage dealt to scouters
        if (!(event.getEntity() instanceof Player player))
            return;
        if (isScouting(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onScoutExhaustion(EntityExhaustionEvent event) {
        // Cancel all exhaustion incurred by scouters
        if (!(event.getEntity() instanceof Player player))
            return;
        if (isScouting(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onScoutDamageEntity(EntityDamageByEntityEvent event) {
        // Cancel all damage dealt by scouters
        if (!(event.getDamager() instanceof Player player))
            return;
        if (isScouting(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onScoutPickup(EntityPickupItemEvent event) {
        // Cancel all item pick-up events from scouters
        if (!(event.getEntity() instanceof Player player))
            return;
        if (isScouting(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onScoutInteractEntity(PlayerInteractEntityEvent event) {
        // Cancel all interactions of entities from scouters (like villager trading)
        if (isScouting(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onScoutInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // If the player is a scouter, cancel the interaction
        if (isScouting(player)) {
            event.setCancelled(true);
            ItemStack item = event.getItem();
            // If the player is confirming start location, set the spawn there and take runners out of scouting mode
            if(item != null && item.getPersistentDataContainer().has(CONFIRM_START_KEY, PersistentDataType.BOOLEAN))
                Core.getInstance().getManhuntManager().handleSpawnSelection(player);
        }
    }

    @EventHandler
    public void onScoutDrop(PlayerDropItemEvent event) {
        // Stop the scout from dropping their star
        if (isScouting(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onScoutMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        if (!isScouting(player)) return;

        Location spawn = player.getWorld().getSpawnLocation();
        Location to = event.getTo();

        int maxChunkDistance = coreConfig.getConfig().getScoutingAllowedChunkRoamDistance();

        double dx = Math.abs(to.getX() - spawn.getX());
        double dz = Math.abs(to.getZ() - spawn.getZ());

        double maxDistance = maxChunkDistance * 16;

        if (dx >= maxDistance || dz >= maxDistance) {

            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();

            if (now - lastWarn.getOrDefault(uuid, 0L) > MESSAGE_COOLDOWN) {
                player.playSound(UI.FAIL_SOUND);
                player.sendMessage(SystemMessage.warn(
                        String.format("You may only scout within {%s} chunks of the original spawn", maxChunkDistance)));
                lastWarn.put(uuid, now);
            }

            event.setCancelled(true);
        }
    }
}
