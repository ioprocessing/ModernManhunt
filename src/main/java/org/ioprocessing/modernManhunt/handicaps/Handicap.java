package org.ioprocessing.modernManhunt.handicaps;

import org.bukkit.entity.Player;

public interface Handicap {

    /**
     * The unique type identifier for this Handicap.
     */
    HandicapType getType();

    /**
     * Whether the handicap is enabled or not.
     */
    boolean isEnabled();

    /**
     * How long this handicap lasts in ticks.
     */
    long getCooldownTicks();

    /**
     * Apply any permanent or startup effects for this handicap (e.g. giving items).
     * Default is no operation for handicaps that have no startup effect.
     */
    default void apply(Player player) {}
}
