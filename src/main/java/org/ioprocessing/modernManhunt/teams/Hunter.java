package org.ioprocessing.modernManhunt.teams;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;
import org.ioprocessing.modernManhunt.config.HandicapConfig;
import org.ioprocessing.modernManhunt.items.ConsumableHeads;
import org.ioprocessing.modernManhunt.items.HunterCompass;

public class Hunter implements Listener {
    public final ConfigManager<CoreConfig> coreConfig;
    public final ConfigManager<HandicapConfig> handicap;



    public Hunter(Core plugin) {
        this.coreConfig = plugin.getCoreConfig();
        this.handicap = plugin.getHandicap();
    }

    public static final NamespacedKey HUNTER_HAS_DIED = new NamespacedKey(Core.getInstance(), "HunterHasDied");

    /**
     * When a hunter dies, drop their head and apply armor handicap if applicable
     */
    @EventHandler
    public void onHunterDeath(PlayerDeathEvent event) {
        if(!Core.getInstance().isActiveManhunt())
            return;
        Player hunter = event.getPlayer();
        if(Core.getInstance().getTeamManager().isHunterTeam(hunter)) {
            // Drop hunter head if applicable
            if(shouldDropHunterHead(hunter))
                hunter.getWorld().dropItemNaturally(hunter.getLocation(), ConsumableHeads.getConsumablePlayerHead(hunter));
            // Mark them as "has died at least once"
            setDied(hunter, true);
        }
    }

    /**
     * Give hunters a compass when they respawn
     */
    @EventHandler
    public void onHunterRespawn(PlayerRespawnEvent event) {
        if(!Core.getInstance().isActiveManhunt() || event.getRespawnReason() != PlayerRespawnEvent.RespawnReason.DEATH)
            return;
        Player hunter = event.getPlayer();
        if(Core.getInstance().getTeamManager().isHunterTeam(hunter)) {
            // Give compass
            ItemStack hunterCompass = HunterCompass.getHunterCompass(hunter);
            LodestoneTracker loc = LodestoneTracker.lodestoneTracker(Core.getInstance().getTeamManager().getCurrentTarget(hunter).getLocation(), false);
            hunterCompass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);

            hunter.give(hunterCompass);
            hunter.updateInventory();
        }
    }

    /**
     * Helper methods for setting/checking if a hunter has previously died
     */
    public static Boolean hasDied(Player player) {
        if(player.getPersistentDataContainer().has(HUNTER_HAS_DIED, PersistentDataType.BOOLEAN))
            return player.getPersistentDataContainer().get(HUNTER_HAS_DIED, PersistentDataType.BOOLEAN);
        return false;
    }

    public static void setDied(Player player, boolean value) {
        player.getPersistentDataContainer().set(HUNTER_HAS_DIED, PersistentDataType.BOOLEAN, value);
    }

    public static void clearDied(Player player) {
        player.getPersistentDataContainer().remove(HUNTER_HAS_DIED);
    }

    private boolean shouldDropHunterHead(Player player) {
        CoreConfig.HeadAdjustments headConfig = coreConfig.getConfig().getHeadAdjustments();
        TeamManager teamManager = Core.getInstance().getTeamManager();
        if(player.getKiller() == null) return false;
        return (headConfig.isEnabled() &&
                teamManager.isHunterTeam(player) &&
                headConfig.isHuntersDropHeads() &&
                (!headConfig.isHuntersOnlyDropFirstDeath() || !Hunter.hasDied(player)) &&
                Core.getInstance().getTeamManager().isRunnerTeam(player.getKiller()));
    }
}
