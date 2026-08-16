package org.ioprocessing.modernManhunt.boons;

import org.bukkit.entity.Player;

import java.util.Map;

public interface Boon {

    /**
     * The unique type identifier for this boon.
     */
    BoonType getType();

    /**
     * Apply the boon to the player. Any state that needs to be restored
     * later (e.g. original scale) should be saved into the provided map
     */
    void apply(Player player, Map<String, Object> savedState);

    /**
     * Undo the boon using whatever was saved into savedState during apply().
     * Called either when the timer expires or when the player rejoins
     * after disconnecting mid-boon.
     */
    void reset(Player player, Map<String, Object> savedState);

    /**
     * How long this boon lasts in ticks.
     */
    long getDurationTicks();

    /**
     * Whether the boon is enabled or not.
     */
    boolean isEnabled();

    /**
     * Whether the boon shows a boss-bar for remaining duration or not.
     */
    boolean isShowBossBar();
}
