package org.ioprocessing.modernManhunt.handicaps;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.HandicapConfig;
import org.ioprocessing.modernManhunt.items.TPStar;

public class TeammateTPHandicap implements Listener, Handicap {
    private final ConfigManager<HandicapConfig> handicap;

    public TeammateTPHandicap(Core core) {
        this.handicap = core.getHandicap();
    }

    @Override
    public HandicapType getType() {
        return HandicapType.TEAMMATE_TP;
    }

    @Override
    public long getCooldownTicks() {
        return getTeammateTPConfig().getTeleportationStarCooldownSeconds() * 20L;
    }

    @Override
    public boolean isEnabled() {return getTeammateTPConfig().isEnabled();}

    @Override
    public void apply(Player player) {
        player.give(TPStar.getTPStar());
    }

    /**
     * If a player has the handicap when they respawn, give them a TP star
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(event.getRespawnReason() != PlayerRespawnEvent.RespawnReason.DEATH)
            return;

        if(!isEnabled()) return;

        Player player = event.getPlayer();
        if(handicap.getConfig().isApplicable(player, HandicapType.TEAMMATE_TP) && Core.getInstance().getTeamManager().isParticipant(player)) {
            player.give(TPStar.getTPStar());
            player.updateInventory();
        }
    }

    private HandicapConfig.TeammateTP getTeammateTPConfig() {
        return handicap.getConfig().getTeammateTp();
    }
}
