package org.ioprocessing.modernManhunt.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.handicaps.HandicapType;
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
public class HandicapConfig {

    private TeammateTP teammateTp = new TeammateTP();
    private HandicapConfig.KeepArmor keepArmor = new HandicapConfig.KeepArmor();
    private HandicapConfig.KeepHotbar keepHotbar = new HandicapConfig.KeepHotbar();
    private HandicapConfig.KeepInventory keepInventory = new HandicapConfig.KeepInventory();
    private HandicapConfig.ExactCoordinates exactCoords = new HandicapConfig.ExactCoordinates();
    private HandicapConfig.FortressCompass fortressCompass = new HandicapConfig.FortressCompass();

    @ConfigSerializable
    @Getter
    @Setter
    public static class TeammateTP { // Give players the ability to teleport to their teammates
        private boolean isEnabled = true;

        private Set<UUID> tpHandicapPlayers = new HashSet<>();

        private int teleportationStarCooldownSeconds = 180;
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class KeepArmor { // Players keep armor on respawn
        private boolean isEnabled = true;

        private Set<UUID> keepArmorPlayers = new HashSet<>();
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class KeepHotbar { // Players keep their hotbar on respawn
        private boolean isEnabled = true;

        private Set<UUID> keepHotbarPlayers = new HashSet<>();
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class KeepInventory { // Players keep their inventory on respawn
        private boolean isEnabled = true;

        private Set<UUID> keepInventoryPlayers = new HashSet<>();
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class ExactCoordinates { // Players can see the exact coordinates when tracking
        private boolean isEnabled = true;

        private Set<UUID> exactCoordinatesPlayers = new HashSet<>();
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class FortressCompass { // Players can see the exact coordinates when tracking
        private boolean isEnabled = true;

        private Set<UUID> fortressCompassPlayers = new HashSet<>();
    }

    public boolean isTPHandicap(UUID uuid) {
        return teammateTp.tpHandicapPlayers.contains(uuid);
    }
    public boolean isKeepArmorHandicap(UUID uuid) {
        return keepArmor.getKeepArmorPlayers().contains(uuid);
    }
    public boolean isKeepHotbarHandicap(UUID uuid) {
        return keepHotbar.getKeepHotbarPlayers().contains(uuid);
    }
    public boolean isKeepInventoryHandicap(UUID uuid) {
        return keepInventory.getKeepInventoryPlayers().contains(uuid);
    }
    public boolean isExactCoordsHandicap(UUID uuid) {
        return exactCoords.exactCoordinatesPlayers.contains(uuid);
    }
    public boolean isFortressCompassHandicap(UUID uuid) {
        return fortressCompass.fortressCompassPlayers.contains(uuid);
    }

    public boolean isApplicable(UUID uuid, HandicapType type) {
        return switch (type) {
            case TEAMMATE_TP -> isTPHandicap(uuid);
            case KEEP_ARMOR -> isKeepArmorHandicap(uuid);
            case KEEP_HOTBAR -> isKeepHotbarHandicap(uuid);
            case KEEP_INVENTORY -> isKeepInventoryHandicap(uuid);
            case EXACT_COORDS -> isExactCoordsHandicap(uuid);
            case FORTRESS_COMPASS -> isFortressCompassHandicap(uuid);
        };
    }


    public boolean isApplicable(Player player, HandicapType type) {
        return isApplicable(player.getUniqueId(), type);
    }

    public void toggle(UUID uuid, HandicapType type) {
        Set<UUID> set = switch (type) {
            case TEAMMATE_TP -> teammateTp.getTpHandicapPlayers();
            case KEEP_ARMOR -> keepArmor.getKeepArmorPlayers();
            case KEEP_HOTBAR -> keepHotbar.getKeepHotbarPlayers();
            case KEEP_INVENTORY -> keepInventory.getKeepInventoryPlayers();
            case EXACT_COORDS -> exactCoords.getExactCoordinatesPlayers();
            case FORTRESS_COMPASS -> fortressCompass.getFortressCompassPlayers();
        };

        if (!set.remove(uuid)) set.add(uuid);
    }
}
