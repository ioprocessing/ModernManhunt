package org.ioprocessing.modernManhunt.handicaps;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.HandicapConfig;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class HandicapManager {

    private final Core core;

    private final Map<HandicapType, Handicap> registry = new EnumMap<>(HandicapType.class);

    public HandicapManager(Core core) {
        this.core = core;
    }

    /**
     * Register a handicap implementation during plugin startup.
     */
    public void register(Handicap handicap) {
        registry.put(handicap.getType(), handicap);
    }

    /**
     * Clears the registry and re-registers all handicaps.
     */
    public void reloadRegistry() {
        registry.clear();
        Core.getInstance().registerHandicaps();
    }

    public Map<HandicapType, Handicap> getRegistry() {
        return Collections.unmodifiableMap(registry);
    }

    /**
     * Apply startup effects for all enabled and applicable handicaps to a player.
     */
    public void applyApplicableHandicaps(Player player) {
        HandicapConfig config = core.getHandicap().getConfig();

        for (Handicap handicap : registry.values()) {
            if (handicap.isEnabled() && config.isApplicable(player, handicap.getType())) {
                handicap.apply(player);
            }
        }

        player.updateInventory();
    }

    /**
     * Apply the cooldown for a handicap to a player.
     * Does nothing if the handicap is not registered or has no cooldown.
     */
    public void applyCooldown(Player player, HandicapType type, NamespacedKey cooldownKey) {
        Handicap handicap = registry.get(type);
        if (handicap == null) {
            Core.logger().warn("Tried to apply cooldown for unregistered handicap: {}", type);
            return;
        }

        long ticks = handicap.getCooldownTicks();
        if (ticks <= 0) return;

        player.setCooldown(cooldownKey, (int) ticks);
    }

    /**
     * Apply the cooldown for a handicap to all members of a group.
     */
    public void applyCooldownToGroup(Iterable<Player> players, HandicapType type, NamespacedKey cooldownKey) {
        for (Player player : players) {
            if (player != null) applyCooldown(player, type, cooldownKey);
        }
    }

    /**
     * Clear the cooldown for a handicap on a player.
     */
    public void clearCooldown(Player player, NamespacedKey cooldownKey) {
        player.setCooldown(cooldownKey, 0);
    }

    /**
     * Check whether a handicap is registered and enabled.
     */
    public boolean isEnabled(HandicapType type) {
        Handicap handicap = registry.get(type);
        return handicap != null && handicap.isEnabled();
    }
}