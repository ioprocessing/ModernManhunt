package org.ioprocessing.modernManhunt.handicaps;

import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.HandicapConfig;

public class ExactCoordsHandicap implements Handicap {
    private final ConfigManager<HandicapConfig> handicap;

    public ExactCoordsHandicap(Core core) {
        this.handicap = core.getHandicap();
    }

    @Override
    public HandicapType getType() {
        return HandicapType.EXACT_COORDS;
    }

    @Override
    public long getCooldownTicks() {
        return 0;
    }

    @Override
    public boolean isEnabled() {return handicap.getConfig().getExactCoords().isEnabled();}

}
