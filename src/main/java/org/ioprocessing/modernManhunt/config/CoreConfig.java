package org.ioprocessing.modernManhunt.config;

import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Configuration for miscellaneous ModernManhunt options
 */
@Setter
@Getter
@ConfigSerializable
public class CoreConfig {

    private CoreConfig.PotionAdjustments potionAdjustments = new CoreConfig.PotionAdjustments();

    private CoreConfig.BedBombingAdjustments bedBombingAdjustments = new CoreConfig.BedBombingAdjustments();

    private CoreConfig.DimensionalTravelAdjustments dimensionalTravelAdjustments = new CoreConfig.DimensionalTravelAdjustments();

    private CoreConfig.HeadAdjustments headAdjustments = new CoreConfig.HeadAdjustments();

    private CoreConfig.ManhuntSpawnWorld manhuntSpawnWorld = new ManhuntSpawnWorld();

    private CoreConfig.Cooldowns cooldowns = new CoreConfig.Cooldowns();

    private CoreConfig.StartConditions startConditions = new CoreConfig.StartConditions();

    private boolean teamColors = false;

    private boolean locatorBarEnabled = false;

    private int scoutingAllowedChunkRoamDistance = 5;

    private boolean resetAttributesOnStart = true;

    private String defaultDifficulty = "EASY";

    @ConfigSerializable
    @Getter
    @Setter
    public static class PotionAdjustments {
        private boolean isEnabled = true;

        // How much each level of strength should increase the player's damage by in half-hearts (vanilla default is 3)
        private int strengthDamageBoostPerLevel = 1;
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class BedBombingAdjustments {
        private boolean isEnabled = true;

        // Bed bombing explosion strength (vanilla default is 5)
        private int explosionStrength = 3;
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class DimensionalTravelAdjustments {
        private boolean isEnabled = true;

        // Nether-to-overworld travel ratio (vanilla default is 8)
        private int overworldToNetherDistanceRatio = 1;
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class HeadAdjustments {
        private boolean isEnabled = true;

        private boolean runnersDropHeads = true;

        private boolean huntersDropHeads = true;

        // Whether hunters only drop their heads on the first death
        private boolean huntersOnlyDropFirstDeath = true;
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class ManhuntSpawnWorld {
        private boolean isEnabled = false;

        private int manhuntSpawnRadius = 0;

        private String overrideDefaultWorldNamespace = "minecraft";

        private String overrideDefaultWorldName = "overworld";
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class Cooldowns { // Cooldowns for items (in seconds)
        private boolean isEnabled = true;

        private int playerHeadCooldownSeconds = 1;

        private int goldenHeadCooldownSeconds = 15;

        private int enderPearlCooldownSeconds = 15;
    }

    @ConfigSerializable
    @Getter
    @Setter
    public static class StartConditions {
        private boolean disableInteractionUntilRunnerMoveOrHit = true;

        private int startDelaySeconds = 5;

        private int hunterBlockRoamRadius = 3;
    }
}
