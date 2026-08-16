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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeepArmorHandicap implements Listener, Handicap {
    private final Map<UUID, ItemStack[]> respawnArmor = new HashMap<>();
    private final ConfigManager<HandicapConfig> handicap;

    public KeepArmorHandicap(Core core) {
        this.handicap = core.getHandicap();
    }

    @Override
    public HandicapType getType() {
        return HandicapType.KEEP_ARMOR;
    }

    @Override
    public long getCooldownTicks() {
        return 0;
    }

    @Override
    public boolean isEnabled() {return handicap.getConfig().getKeepArmor().isEnabled();}

    /**
     * Retain armor on death if applicable
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(!isEnabled()) return;

        Player player = event.getEntity();

        if(isKeepArmorHandicap(player)) // Retain their armor if handicap active
            retainArmor(player, event);
    }

    /**
     * Return armor on death if applicable
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(!isEnabled()) return;

        Player player = event.getPlayer();

        if (isKeepArmorHandicap(player))
            returnArmor(player);
    }

    private void retainArmor(Player player, PlayerDeathEvent event) {
        // Get all their drops and remove all the armor pieces they're wearing
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack armorPiece : armor) { // Delete them from drops
            event.getDrops().remove(armorPiece);
        }
        // Add all armor pieces to map with the player ID
        respawnArmor.put(player.getUniqueId(), armor.clone());
    }

    private void returnArmor(Player player) {
        ItemStack[] armor = respawnArmor.remove(player.getUniqueId());
        if (armor != null) {
            player.getInventory().setArmorContents(armor);
            player.updateInventory();
        }
    }

    private boolean isKeepArmorHandicap(Player player) {
       return handicap
               .getConfig()
               .getKeepArmor()
               .getKeepArmorPlayers()
               .contains(player.getUniqueId());
    }
}
