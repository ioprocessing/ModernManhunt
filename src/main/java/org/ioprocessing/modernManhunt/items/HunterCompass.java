package org.ioprocessing.modernManhunt.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.teams.TeamManager;

import java.util.Arrays;
import java.util.List;

public class HunterCompass implements Listener {
    public static final NamespacedKey HUNTER_COMPASS_KEY = new NamespacedKey(Core.getInstance(), "HunterCompass");
    public static final NamespacedKey TARGETED_PLAYER = new NamespacedKey(Core.getInstance(), "TargetedPlayer");

    /**
     * @return Hunter Compass item
     */
    public static ItemStack getHunterCompass() {
        return getHunterCompass(null);
    }

    /**
     * @return Hunter Compass item (with target pre-selected)
     */
    public static ItemStack getHunterCompass(Player hunter) {
        ItemStack hunterCompass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) hunterCompass.getItemMeta();

        // Custom item properties
        TextComponent compassName = Component.text("Hunter's Compass", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false);
        meta.lore(compassLore(Core.getInstance().getTeamManager().getCurrentTarget(hunter)));

        meta.getPersistentDataContainer().set(HUNTER_COMPASS_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.PREVENT_ITEM_DROP, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        hunterCompass.setItemMeta(meta);

        hunterCompass.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker().build());
        hunterCompass.setData(DataComponentTypes.CUSTOM_NAME, compassName); // Unfortunately lodestone tracker overrides item name to "lodestone compass"
        hunterCompass.setData(DataComponentTypes.RARITY, ItemRarity.RARE);

        return hunterCompass;
    }

    /**
     * Handles target selection and compass tracking
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCompassUse(PlayerInteractEvent event) {
        Player clicker = event.getPlayer();
        if (event.getItem() == null) return;

        ItemStack hunterCompass = event.getItem();

        if (!CustomItems.hasKey(hunterCompass, HUNTER_COMPASS_KEY)) return;

        // Left click
        if (event.getAction().isLeftClick()) {
            handleHunterCompassLeftClick(clicker, hunterCompass);
            return;
        }

        Player target = Core.getInstance().getTeamManager().getCurrentTarget(clicker);
        if (target == null) {
            clicker.sendActionBar(Component.text("No target found", NamedTextColor.RED));
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
            return;
        }

        // Right click
        if (target.getWorld().equals(clicker.getWorld())) {
            handleHunterCompassRightClick(clicker, hunterCompass);
        } else {
            clicker.sendActionBar(Component.text("Target not found", NamedTextColor.RED));
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
        }
    }

    private void handleHunterCompassLeftClick(Player clicker, ItemStack hunterCompass) {
        Player target = cycleTarget(clicker);
        if (target == null) {
            clicker.sendActionBar(Component.text("No target found", NamedTextColor.RED));
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
            return;
        }

        ItemMeta meta = hunterCompass.getItemMeta();
        meta.lore(compassLore(target));
        hunterCompass.setItemMeta(meta);

        NamedTextColor targetColor = NamedTextColor.GREEN;
        if (target.getWorld().equals(clicker.getWorld())) {
            LodestoneTracker loc = LodestoneTracker.lodestoneTracker(target.getLocation(), false);
            hunterCompass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 0.5f);
        } else {
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
            targetColor = NamedTextColor.RED;
        }

        clicker.sendActionBar(targetingActionBarText(target, targetColor, isExactCoordsHandicap(clicker)));
    }

    private void handleHunterCompassRightClick(Player clicker, ItemStack hunterCompass) {
        TeamManager teamManager = Core.getInstance().getTeamManager();
        Player target = teamManager.getCurrentTarget(clicker);

        if (target.getWorld().equals(clicker.getWorld())) {
            LodestoneTracker loc = LodestoneTracker.lodestoneTracker(teamManager.getCurrentTarget(clicker).getLocation(), false);
            hunterCompass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.1f, 1f); // Update the compass to their current location
        } else {
            clicker.playSound(clicker.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
            clicker.sendActionBar(Component.text("Target not found", NamedTextColor.RED));
            return;
        }

        clicker.sendActionBar(targetingActionBarText(target, NamedTextColor.GREEN, isExactCoordsHandicap(clicker)));
    }

    private Player cycleTarget(Player hunter) {
        List<Player> runners = Core.getInstance().getTeamManager().getRunners();
        if (runners.isEmpty())
            return null;

        Integer index = hunter.getPersistentDataContainer()
                .get(HunterCompass.TARGETED_PLAYER, PersistentDataType.INTEGER);

        if (index == null)
            index = 0;

        index = (index + 1) % runners.size();

        hunter.getPersistentDataContainer().set(
                HunterCompass.TARGETED_PLAYER,
                PersistentDataType.INTEGER,
                index
        );

        return runners.get(index);
    }

    private static List<TextComponent> compassLore(Player player) {
        Component targetedPlayer = player == null ?
                Component.text("None").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                : Component.text(player.getName()).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false);
        return Arrays.asList(
                Component.text("A compass that points toward the runner.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text()
                        .append(Component.text("CURRENT TARGET: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(targetedPlayer)
                        .build(),
                Component.text()
                        .append(Component.text("RIGHT CLICK").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to refresh target location").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text()
                        .append(Component.text("LEFT CLICK").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to change current target").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build());
    }

    /** Send formatted text for the action bar, including coordinates if the player has that handicap */
    private Component targetingActionBarText(Player target, NamedTextColor color, boolean exactCoords) {
        TextComponent.Builder builder = Component.text()
                .append(Component.text("Targeting: ").color(color))
                .append(Component.text(target.getName()).color(color));
        if(color == NamedTextColor.RED)
            builder.append(Component.text(" (not found)").color(color));
        else if(exactCoords) {
            Location loc = target.getLocation();
            int X = loc.getBlockX();
            int Y = loc.getBlockY();
            int Z = loc.getBlockZ();
            builder.append(Component.text(String.format(" at X: %s Y: %s Z: %s", X, Y, Z), NamedTextColor.WHITE));
        }
        return builder.build();
    }

    private boolean isExactCoordsHandicap(Player player) {
        return Core.getInstance().getHandicap().getConfig().isExactCoordsHandicap(player.getUniqueId());
    }
}
