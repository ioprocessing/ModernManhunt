package org.ioprocessing.modernManhunt.handicaps;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.HandicapConfig;
import org.ioprocessing.modernManhunt.items.CustomItems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeepHotbarHandicap implements Listener, Handicap {
    private final Map<UUID, ItemStack[]> respawnHotbar = new HashMap<>();
    private final ConfigManager<HandicapConfig> handicap;

    public KeepHotbarHandicap(Core core) {
        this.handicap = core.getHandicap();
    }

    @Override
    public HandicapType getType() {
        return HandicapType.KEEP_HOTBAR;
    }

    @Override
    public long getCooldownTicks() {
        return 0;
    }

    @Override
    public boolean isEnabled() {return handicap.getConfig().getKeepHotbar().isEnabled();}

    /**
     * Retain hotbar on death if applicable
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!isEnabled()) return;

        Player player = event.getEntity();

        if(isKeepHotbarHandicap(player))
            retainHotbar(player, event);
    }

    /**
     * Return hotbar on death if applicable
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();

        if (isKeepHotbarHandicap(player))
            returnHotbar(player);
    }

    private void retainHotbar(Player player, PlayerDeathEvent event) {
        ItemStack[] hotbar = new ItemStack[10]; // 0-8 hotbar, 9 = offhand
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (!CustomItems.isPreventItemDrop(item))
                hotbar[i] = item;
        }
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (!CustomItems.isPreventItemDrop(offhand))
            hotbar[9] = offhand;

        for (int i = 0; i < 10; i++)
            event.getDrops().remove(hotbar[i]);

        respawnHotbar.put(player.getUniqueId(), hotbar.clone());
    }

    private void returnHotbar(Player player) {
        ItemStack[] hotbar = respawnHotbar.remove(player.getUniqueId());
        if (hotbar != null) {
            for (int i = 0; i < 9; i++)
                player.getInventory().setItem(i, hotbar[i]);
            player.getInventory().setItemInOffHand(hotbar[9]);
            player.updateInventory();
        }
    }

    private boolean isKeepHotbarHandicap(Player player) {
        return handicap
                .getConfig()
                .getKeepHotbar()
                .getKeepHotbarPlayers()
                .contains(player.getUniqueId());
    }
}
