package org.ioprocessing.modernManhunt.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.boons.BoonType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Configuration for handicaps and which players are using them
 */
@Setter
@Getter
@ConfigSerializable
public class BoonConfig {

    private BoonConfig.Size size = new BoonConfig.Size();
    private BoonConfig.Speed speed = new BoonConfig.Speed();
    private BoonConfig.Head head = new BoonConfig.Head();

    @ConfigSerializable
    @Getter
    @Setter
    public static class Size { // Set the player's size attribute for the duration
        private boolean isEnabled = true;

        private boolean isShowBossBar = true;

        private double sizeAdjustment = 0.5;

        private int durationSeconds = 180;

        private Set<UUID> sizeBoonPlayers = new HashSet<>();
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class Speed { // Set the player's size attribute for the duration
        private boolean isEnabled = true;

        private boolean isShowBossBar = false;

        private double speedAdjustment = 0.12;

        private int durationSeconds = 180;

        private Set<UUID> speedBoonPlayers = new HashSet<>();
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class Head { // Set the player's starting item
        private boolean isEnabled = true;

        private Set<UUID> headBoonPlayers = new HashSet<>();
    }

    public boolean isSizeBoon(UUID uuid) {
        return size.sizeBoonPlayers.contains(uuid);
    }
    public boolean isSpeedBoon(UUID uuid) {
        return speed.speedBoonPlayers.contains(uuid);
    }
    public boolean isHeadBoon(UUID uuid) {
        return head.headBoonPlayers.contains(uuid);
    }

    public boolean isApplicable(UUID uuid, BoonType type) {
        return switch (type) {
            case SIZE -> isSizeBoon(uuid);
            case SPEED -> isSpeedBoon(uuid);
            case HEAD -> isHeadBoon(uuid);
        };
    }

    public boolean isApplicable(Player player, BoonType type) {
        return isApplicable(player.getUniqueId(), type);
    }

    public void toggle(UUID uuid, BoonType type) {
        Set<UUID> set = switch (type) {
            case SIZE -> size.getSizeBoonPlayers();
            case SPEED -> speed.getSpeedBoonPlayers();
            case HEAD -> head.getHeadBoonPlayers();
        };

        if (!set.remove(uuid)) set.add(uuid);
    }
}
