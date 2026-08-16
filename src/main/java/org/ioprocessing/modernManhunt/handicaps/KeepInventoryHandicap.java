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

public class KeepInventoryHandicap implements Listener, Handicap {
    private final Map<UUID, ItemStack[]> respawnInventory = new HashMap<>();
    private final Map<UUID, int[]> respawnExp = new HashMap<>();
    private final ConfigManager<HandicapConfig> handicap;

    public KeepInventoryHandicap(Core core) {
        this.handicap = core.getHandicap();
    }

    @Override
    public HandicapType getType() {
        return HandicapType.KEEP_INVENTORY;
    }

    @Override
    public long getCooldownTicks() {
        return 0;
    }

    @Override
    public boolean isEnabled() {return handicap.getConfig().getKeepInventory().isEnabled();}

    /**
     * Retain full inventory, armor, and experience on death if applicable
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!isEnabled()) return;

        Player player = event.getEntity();

        if (isKeepInventoryHandicap(player))
            retainAll(player, event);
    }

    /**
     * Return full inventory, armor, and experience on respawn if applicable
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!isEnabled()) return;

        Player player = event.getPlayer();

        if (isKeepInventoryHandicap(player))
            returnAll(player);
    }

    private void retainAll(Player player, PlayerDeathEvent event) {
        // Main inventory slots 0-35 (0-8 hotbar, 9-35 main), 36-39 armor, 40 offhand
        ItemStack[] saved = new ItemStack[41];

        // Hotbar + main inventory
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (!CustomItems.isPreventItemDrop(item))
                saved[i] = item;
        }

        // Armor slots
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < 4; i++) {
            if (!CustomItems.isPreventItemDrop(armor[i]))
                saved[36 + i] = armor[i];
        }

        // Offhand
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (!CustomItems.isPreventItemDrop(offhand))
            saved[40] = offhand;

        // Remove all saved items from the drop list
        for (ItemStack item : saved)
            event.getDrops().remove(item);

        // Save experience
        respawnExp.put(player.getUniqueId(), new int[]{player.getTotalExperience(), player.getLevel(), (int)(player.getExp() * player.getExpToLevel())});

        // Suppress vanilla XP drops
        event.setDroppedExp(0);

        respawnInventory.put(player.getUniqueId(), saved);
    }

    private void returnAll(Player player) {
        ItemStack[] saved = respawnInventory.remove(player.getUniqueId());
        if (saved != null) {
            // Restore hotbar + main inventory
            for (int i = 0; i < 36; i++)
                player.getInventory().setItem(i, saved[i]);

            // Restore armor
            ItemStack[] armor = new ItemStack[]{saved[36], saved[37], saved[38], saved[39]};
            player.getInventory().setArmorContents(armor);

            // Restore offhand
            player.getInventory().setItemInOffHand(saved[40]);

            player.updateInventory();
        }

        // Restore experience
        int[] exp = respawnExp.remove(player.getUniqueId());
        if (exp != null) {
            player.setTotalExperience(exp[0]);
            player.setLevel(exp[1]);
            player.setExp(exp[2] / (float) player.getExpToLevel());
        }
    }

    private boolean isKeepInventoryHandicap(Player player) {
        return handicap
                .getConfig()
                .getKeepInventory()
                .getKeepInventoryPlayers()
                .contains(player.getUniqueId());
    }
}
