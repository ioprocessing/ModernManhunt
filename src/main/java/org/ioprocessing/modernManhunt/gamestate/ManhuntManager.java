package org.ioprocessing.modernManhunt.gamestate;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.boons.BoonManager;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.config.CoreConfig;
import org.ioprocessing.modernManhunt.handicaps.HandicapManager;
import org.ioprocessing.modernManhunt.items.HunterCompass;
import org.ioprocessing.modernManhunt.menus.UI;
import org.ioprocessing.modernManhunt.teams.Hunter;
import org.ioprocessing.modernManhunt.teams.Runner;
import org.ioprocessing.modernManhunt.teams.Spectator;
import org.ioprocessing.modernManhunt.teams.TeamManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles player setup and tracks manhunt state
 */
public class ManhuntManager {
    private final ScoutParticlesTask scoutParticlesTask;

    private long delayEndTick = -1;

    public ManhuntManager(Core core) {
        this.scoutParticlesTask = new ScoutParticlesTask();
    }

    @Setter
    private ManhuntState manhuntState = ManhuntState.ENDED;

    public ManhuntState getState() {
        return manhuntState;
    }

    public void startManhunt(World startingWorld) {
        showPreTeleportTitle(getParticipants());

        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            teleportToSpawnAndReset(getParticipants(), startingWorld);

            setupHunterTeam();
            setupRunnerTeam();
            setupSpectators();

            for(World manhuntWorld : Core.getManhuntWorlds()) {
                manhuntWorld.setGameRule(GameRules.ADVANCE_TIME, true);
            }

            if (isDelayStart()) {
                int startDelaySeconds = Core.getInstance()
                        .getCoreConfig()
                        .getConfig()
                        .getStartConditions()
                        .getStartDelaySeconds();
                if(startDelaySeconds > 0) {
                    setManhuntState(ManhuntState.WAITING_FOR_DELAY);

                    long durationTicks = startDelaySeconds * 20L;
                    createDelayBossBar(durationTicks);

                    Bukkit.getScheduler().runTaskLater(Core.getInstance(), () ->
                                    setManhuntState(ManhuntState.STARTING), durationTicks);
                } else setManhuntState(ManhuntState.STARTING);

            } else {
                setManhuntState(ManhuntState.RUNNING);
            }
        }, 100L);
    }

    /** 0 = no winner, 1 = runners win, 2 = hunters win */
    public void endManhunt(int winner) {
        if(winner != 0) {
            for (Player player : getParticipants()) {
                if (winner == 2)
                    player.showTitle(UI.huntersWinTitle());
                if (winner == 1)
                    player.showTitle(UI.runnersWinTitle());
                player.playSound(UI.MANHUNT_END_SOUND);
                if(Runner.isDead(player)) {
                    Spectator.stopSpectating(player);
                    Core.getInstance().getTeamManager().setRunnerTeam(player);
                }
            }
        }

        for(Player player : Core.getInstance().getTeamManager().getSpectators()) {
            Spectator.stopSpectating(player);
        }

        CoreConfig.ManhuntSpawnWorld manhuntSpawnWorld = Core.getInstance()
                .getCoreConfig()
                .getConfig()
                .getManhuntSpawnWorld();

        if(manhuntSpawnWorld.isEnabled()) {
            String namespace = manhuntSpawnWorld.getOverrideDefaultWorldNamespace();
            String name = manhuntSpawnWorld.getOverrideDefaultWorldName();
            NamespacedKey defaultRespawnWorld = new NamespacedKey(namespace, name);

            World respawnWorld = Bukkit.getWorld(defaultRespawnWorld);
            if (respawnWorld != null)
                Bukkit.getServer().setRespawnWorld(respawnWorld);
            else Core.logger().error("Failed to set respawn world to default namespaced key {}", defaultRespawnWorld);
        }

        setManhuntState(ManhuntState.ENDED);
    }

    public void startScoutingPhase(World world) {
        Core.getInstance().getManhuntManager().setManhuntState(ManhuntState.SCOUTING);
        for (Player runner : Core.getInstance().getTeamManager().getRunners()) {
            runner.teleport(world.getSpawnLocation());
            startPlayerAsScout(runner);
        }
        scoutParticlesTask.startScoutingParticles(world);
    }

    public void stopScoutingPhase(World world) {
        setManhuntState(ManhuntState.WAITING_FOR_TELEPORT);
        scoutParticlesTask.stopScoutingParticles();
        startManhunt(world);
    }

    /**
     * Initialize all hunters by showing them title, teleporting them, and resetting their player-state
     */
    private void setupHunterTeam() {
        for (Player hunter : Core.getInstance().getTeamManager().getHunters())
            if(hunter.isOnline())
                giveHunterCompass(hunter);

        // Disable collision for hunters (amongst each other) until the game starts
        if (isDelayStart())
            Core.getInstance()
                    .getTeamManager()
                    .getHunterTeam()
                    .getScoreboardTeam()
                    .setOption(
                            Team.Option.COLLISION_RULE,
                            Team.OptionStatus.FOR_OTHER_TEAMS);
    }

    /**
     * Initialize all runners by showing them title, teleporting them, and resetting their player-state
     */
    private void setupRunnerTeam() {
            if (isDelayStart())
                Core.getInstance()
                        .getTeamManager()
                        .getRunnerTeam()
                        .getScoreboardTeam()
                        .setOption(
                                Team.Option.COLLISION_RULE,
                                Team.OptionStatus.NEVER);
    }

    /**
     * Put players into custom spectator mode and hide them from other players
     */
    private void setupSpectators() {
        for(Player participant : getParticipants()) {
            if(participant.isOnline() && Core.getInstance().getTeamManager().isSpectatorTeam(participant))
                Spectator.startSpectating(participant);
            // If they're not on the spectator team but marked as spectating, stop them
            else if(Spectator.isSpectating(participant)) Spectator.stopSpectating(participant);
        }
    }

    /**
     * Give hunters a pre-set tracking compass.
     */
    private void giveHunterCompass(Player player) {
        ItemStack hunterCompass = HunterCompass.getHunterCompass(player);
        LodestoneTracker loc = LodestoneTracker.lodestoneTracker(Core.getInstance().getTeamManager().getCurrentTarget(player).getLocation(), false);
        hunterCompass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);

        player.give(hunterCompass);
        player.updateInventory();
    }

    public void startPlayerAsScout(Player player) {
        resetVitality(player);

        player.getInventory().clear();
        player.setExperienceLevelAndProgress(0);

        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlySpeed(0.15f);

        Core.getInstance().getBoonManager().cancelAllBoons(player);

        player.sendActionBar(Component.text("You are now scouting. Select a start location", NamedTextColor.GREEN));
        player.give(Runner.getConfirmStart());
        player.updateInventory();

        Runner.setScouting(player, true);
    }

    private void showPreTeleportTitle(List<Player> players) {
        for (Player player : players) {
            if (player == null) continue;
            player.playSound(UI.SUCCESS_SOUND);
            player.showTitle(UI.teleportTitle());
        }
    }

    private void teleportToSpawnAndReset(List<Player> players, World world) {
        for(Player player : players) {
            if (player == null) continue;
            player.teleport(world.getSpawnLocation().add(0.5, 0, 0.5));
            player.playSound(UI.TELEPORT_SOUND);
            resetPlayer(player);
        }
    }

    /// RESET PLAYERS BEFORE STARTING

    public void resetPlayer(Player player) {
        player.getInventory().clear();
        player.updateInventory();

        player.clearActivePotionEffects();
        player.setExperienceLevelAndProgress(0);
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setInvisible(false);
        player.setInvulnerable(false);
        player.setFlySpeed(0.1f);

        revokeAdvancements(player);

        Hunter.clearDied(player);
        Runner.clearScouting(player);
        Core.getInstance().getBoonManager().cancelAllBoons(player);

        resetVitality(player);

        if(isResetAttributes())
            resetAttributes(player);

        applyBoons(player);
        applyHandicaps(player);
    }

    /**
     * Reset the player's attributes back to vanilla values.
     */
    private void resetAttributes(Player player) {
        // Use PLAYER attribute defaults or else it defaults to some wack shit values
        Attributable playerDefaults = EntityType.PLAYER.getDefaultAttributes();

        for (Attribute attribute : Registry.ATTRIBUTE.stream().toList()) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;

            AttributeInstance defaultInstance = playerDefaults.getAttribute(attribute);
            double defaultValue = (defaultInstance != null)
                    ? defaultInstance.getBaseValue()
                    : instance.getDefaultValue();

            instance.setBaseValue(defaultValue);

            for (AttributeModifier modifier : instance.getModifiers()) {
                instance.removeModifier(modifier);
            }
        }
    }

    private void applyBoons(Player player) {
        BoonManager boonManager = Core.getInstance().getBoonManager();
        boonManager.applyApplicableBoons(player);
        if(!isDelayStart())
            boonManager.startAllCountdowns(player);
    }

    private void applyHandicaps(Player player) {
        HandicapManager handicapManager = Core.getInstance().getHandicapManager();
        handicapManager.applyApplicableHandicaps(player);
    }

    private void revokeAdvancements(Player player) {
        Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext())
        {
            AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
            for (String criteria : progress.getAwardedCriteria())
                progress.revokeCriteria(criteria);
        }
    }

    /// SPAWN HANDLING

    /**
     * Handle logic for setting the spawn when a scout clicks the star
     * @param player The player who set the spawn location
     */
    public void handleSpawnSelection(Player player) {
        if(!isSafeSpawn(player.getLocation())) {
            player.sendMessage(SystemMessage.error("Start location must be above ground and on a solid block"));
            return;
        }

        Location loc = player.getLocation();
        // Lock spawn to a cardinal direction angle
        float yaw = (Math.round(loc.getYaw() / 90f) * 90f) % 360;

        // Ensure spawn is in the center of a block
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        // Floor might cause issues, investigate later
        Location spawn = new Location(loc.getWorld(), x, Math.floor(y), z, yaw, 0f);

        player.getWorld().setSpawnLocation(spawn);
        stopScoutingPhase(player.getWorld());

        // Remove all spawn location selectors from scout inventories
        for(Player scout : Core.getInstance().getTeamManager().getRunners()) {
            if(Runner.isScouting(scout)) {
                scout.getInventory().clear();
                scout.updateInventory();
            }
        }
    }

    /// HELPERS

    private boolean isSafeSpawn(Location loc) {
        Block feet = loc.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Block ground = feet.getRelative(BlockFace.DOWN);

        return feet.isPassable() && !feet.isLiquid()
                && head.isPassable() && !head.isLiquid()
                && ground.getType().isSolid();
    }

    private boolean isDelayStart() {
        return Core.getInstance()
                .getCoreConfig()
                .getConfig()
                .getStartConditions()
                .isDisableInteractionUntilRunnerMoveOrHit();
    }

    private boolean isResetAttributes() {
        return Core.getInstance()
                .getCoreConfig()
                .getConfig()
                .isResetAttributesOnStart();
    }

    private List<Player> getParticipants() {
        TeamManager teamManager = Core.getInstance().getTeamManager();

        List<Player> participants = new ArrayList<>();
        participants.addAll(teamManager.getRunners());
        participants.addAll(teamManager.getHunters());
        participants.addAll(teamManager.getSpectators());
        return participants;
    }

    private void resetVitality(Player player) {
        player.setSaturation(20.0f);
        player.setFoodLevel(20);
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if(maxHealth != null)
            player.setHealth(maxHealth.getValue());
    }

    private void createDelayBossBar(long durationTicks) {
        delayEndTick = Bukkit.getServer().getCurrentTick() + durationTicks;

        BossBar delayBar = BossBar.bossBar(
                Component.text("Starting in " + durationTicks / 20 + " second(s)"),
                1.0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );

        long[] ticksElapsed = {0};
        BukkitTask[] barTask = {null};

        for (Player participant : getParticipants()) {
            participant.showBossBar(delayBar);
        }

        barTask[0] = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            ticksElapsed[0]++;
            float progress = 1.0f - ((float) ticksElapsed[0] / durationTicks);

            if (progress <= 0) {
                delayEndTick = -1;
                for (Player participant : getParticipants()) {
                    participant.hideBossBar(delayBar);
                }
                barTask[0].cancel();
                return;
            }

            long secondsLeft = (durationTicks - ticksElapsed[0]) / 20;
            delayBar.name(Component.text("Starting in " + secondsLeft + " second(s)"));
            delayBar.progress(Math.max(0f, progress));
        }, 0L, 1L);
    }

    public long getDelaySecondsRemaining() {
        if (delayEndTick < 0) return 0;
        long ticksLeft = delayEndTick - Bukkit.getServer().getCurrentTick();
        return Math.max(0, ticksLeft / 20);
    }
}
