package org.ioprocessing.modernManhunt.handicaps;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.HandicapConfig;
import org.ioprocessing.modernManhunt.items.FortressCompass;

public class FortressCompassHandicap implements Handicap, Listener {
    private final ConfigManager<HandicapConfig> handicap;

    public FortressCompassHandicap(Core core) {
        this.handicap = core.getHandicap();
    }

    @Override
    public HandicapType getType() {
        return HandicapType.FORTRESS_COMPASS;
    }

    @Override
    public long getCooldownTicks() {
        return 0;
    }

    @Override
    public boolean isEnabled() {return handicap.getConfig().getFortressCompass().isEnabled();}

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if(!isEnabled()) return;

        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();

        if(isFortressCompassHandicap(player)) return;

        // Check if it's the specific advancement
        if (advancement.getKey().toString().equals("minecraft:story/enter_the_nether")) {
            player.give(FortressCompass.getFortressCompass());
        }
    }

    private boolean isFortressCompassHandicap(Player player) {
        return handicap
                .getConfig()
                .getFortressCompass()
                .getFortressCompassPlayers()
                .contains(player.getUniqueId());
    }
}
