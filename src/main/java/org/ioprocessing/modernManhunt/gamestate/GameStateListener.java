package org.ioprocessing.modernManhunt.gamestate;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;
import org.ioprocessing.modernManhunt.items.ConsumableHeads;
import org.ioprocessing.modernManhunt.menus.UI;
import org.ioprocessing.modernManhunt.teams.Runner;
import org.ioprocessing.modernManhunt.teams.Spectator;
import org.ioprocessing.modernManhunt.teams.TeamManager;

import java.util.*;

/**
 * Manages respawning mid-manhunt and tracks manhunt gamestate
 */
public class GameStateListener implements Listener {
    private final Map<UUID, Long> lastWarn = new HashMap<>(); // Players who have received a warning message recently
    private static final long MESSAGE_COOLDOWN = 1000;

    private final ConfigManager<CoreConfig> coreConfig;

    public GameStateListener(Core plugin) {
        this.coreConfig = plugin.getCoreConfig();
    }

    /**
     * When the ender-dragon dies, end the manhunt
     */
    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        if(!Core.getInstance().isActiveManhunt())
            return;
        Entity e = event.getEntity();
        if (e.getType().equals(EntityType.ENDER_DRAGON)) {
            // If any of the players marked as runners are alive, end the game
            if(!Core.getInstance().getTeamManager().getRunners().isEmpty())
                Core.getInstance().getManhuntManager().endManhunt(1);
        }
    }

    /**
     * When a runner dies, set them in spectator or end the manhunt
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRunnerDeath(PlayerDeathEvent event) {
        Player victim = event.getPlayer();

        if (!Core.getInstance().getTeamManager().isRunnerTeam(victim)
                || !Core.getInstance().isActiveManhunt()) return;

        long remainingOnlineRunners = Core.getInstance().getTeamManager().getOnlineRunnerCount() - 1;

        if (remainingOnlineRunners <= 0) {
            Core.getInstance().getManhuntManager().endManhunt(2);
            return;
        }

        // Grab the death message
        Component deathMessage = event.deathMessage();

        // Suppress vanilla death & respawn by cancelling
        event.setCancelled(true);

        // Broadcast it ourselves
        if (deathMessage != null) {
            Bukkit.broadcast(deathMessage);
        }

        handleRunnerDeath(victim);
    }

    /**
     * Internal handler for a runner death (that doesn't end the manhunt)
     */
    private void handleRunnerDeath(Player player) {
        // Drop their items and strike lightning
        dropItems(player);
        player.getWorld().strikeLightningEffect(player.getLocation());

        // Then put them in spectator mode
        Core.getInstance().getTeamManager().removeRunner(player);
        Spectator.startSpectating(player);
        player.showTitle(UI.runnerDiesTitle());
    }


    private void dropItems(Player player) {
        World world = player.getWorld();

        Location dropLoc = player.getLocation();

        boolean hasStorage = false;
        for (ItemStack it : player.getInventory().getStorageContents()) {
            if (it != null && it.getType() != Material.AIR) { hasStorage = true; break; }
        }
        boolean hasArmor = false;
        for (ItemStack it : player.getInventory().getArmorContents()) {
            if (it != null && it.getType() != Material.AIR) { hasArmor = true; break; }
        }
        boolean hasOffhand = player.getInventory().getItemInOffHand().getType() != Material.AIR;

        boolean hadAnything = hasStorage || hasArmor || hasOffhand;

        // Drop head if enabled
        if(shouldDropRunnerHead(player)) {
            world.dropItemNaturally(dropLoc, ConsumableHeads.getConsumablePlayerHead(player));
        }

        if (hadAnything) {
            for (ItemStack item : player.getInventory().getStorageContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (item.getEnchantmentLevel(Enchantment.VANISHING_CURSE) > 0) continue;
                    world.dropItemNaturally(dropLoc, item.clone());
                }
            }
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (item.getEnchantmentLevel(Enchantment.VANISHING_CURSE) > 0) continue;
                    world.dropItemNaturally(dropLoc, item.clone());
                }
            }
            if (hasOffhand) {
                ItemStack offhand = player.getInventory().getItemInOffHand();
                if (offhand.getEnchantmentLevel(Enchantment.VANISHING_CURSE) == 0) {
                    world.dropItemNaturally(dropLoc, offhand.clone());
                }
            }
        }
    }

    private boolean shouldDropRunnerHead(Player player) {
        CoreConfig.HeadAdjustments headConfig = coreConfig.getConfig().getHeadAdjustments();
        TeamManager teamManager = Core.getInstance().getTeamManager();
        return (headConfig.isEnabled() && teamManager.isRunnerTeam(player) && headConfig.isRunnersDropHeads());
    }


    private void startManhuntRunning() {
        ManhuntManager manhunt = Core.getInstance().getManhuntManager();
        TeamManager team = Core.getInstance().getTeamManager();

        team.getRunnerTeam().getScoreboardTeam().setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
        team.getHunterTeam().getScoreboardTeam().setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);

        List<Player> participants = new ArrayList<>();
        participants.addAll(team.getRunners());
        participants.addAll(team.getHunters());

        for(Player participant : participants) {
            startBoonCountdown(participant);
        }

        manhunt.setManhuntState(ManhuntState.RUNNING);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setScoreboard(Core.getInstance().getTeamManager().board);
    }

    /// DELAY
    ///
    /// Stop the runners from moving or otherwise performing actions aside from turning until the delay ends.
    /// TODO: Tell the player exactly how long they need to wait to perform actions

    @EventHandler(priority = EventPriority.LOW)
    public void onDelayedMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;

        Player player = event.getPlayer();

        if(!isWaitingForDelay()) return;

        if(!isRunnerTeam(player)) return;

        long secondsLeft = Core.getInstance().getManhuntManager().getDelaySecondsRemaining();
        warnIfApplicable(player, String.format("You must wait {%d second(s)} to move",  secondsLeft)); // TODO add conditional formatting (s) based on 1 second or more
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDelayedAttack(PrePlayerAttackEntityEvent event) {
        Player attacker = event.getPlayer();

        if(!isWaitingForDelay()) return;

        if(!isRunnerTeam(attacker)) return;

        long secondsLeft = Core.getInstance().getManhuntManager().getDelaySecondsRemaining();
        warnIfApplicable(attacker, String.format("You must wait {%d second(s)} to attack",  secondsLeft));
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDelayedPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        if(!isWaitingForDelay()) return;

        if(isRunnerTeam(player)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDelayedInteractEntity(PlayerInteractEntityEvent event) {

        if(!isWaitingForDelay()) return;

        if(isRunnerTeam(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDelayedInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!isWaitingForDelay()) return;

        if(isRunnerTeam(player)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTargetDelayed(EntityTargetLivingEntityEvent event) {
        if(!(event.getTarget() instanceof Player player)) return;

        if(!isWaitingForDelay()) return;

        if(isRunnerTeam(player)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDelayedDamaged(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;

        if(!isWaitingForDelay()) return;

        if(isRunnerTeam(player)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDelayedHunterMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;
        if (!isWaitingForDelay()) return;

        Player player = event.getPlayer();
        if (!isHunterTeam(player)) return;

        Location spawn = player.getWorld().getSpawnLocation();
        int maxDistance = coreConfig.getConfig()
                .getStartConditions()
                .getHunterBlockRoamRadius();

        if (event.getTo().distanceSquared(spawn) > (double) maxDistance * (double) maxDistance) {
            warnIfApplicable(player, String.format("You may only move within {%s} blocks of spawn until the Manhunt begins", maxDistance));
            event.setCancelled(true);
        }
    }

    /// STARTING

    /**
     * Prevent hunters from attacking the runner until the Manhunt starts.
     * When a runner attacks a player, start the Manhunt.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onStartingAttack(PrePlayerAttackEntityEvent event) {

        Player attacker = event.getPlayer();
        if(!(event.getAttacked() instanceof Player victim)) return;

        if(!starting()) return;

        if(isHunterTeam(attacker) && isRunnerTeam(victim)) event.setCancelled(true);
        else if (isRunnerTeam(attacker)) startManhuntRunning();
    }

    /**
     * The first time a runner changes physical position, start the Manhunt.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onStartingMove(PlayerMoveEvent event) {
        if (!event.hasChangedPosition()) return;

        Player player = event.getPlayer();
        TeamManager team = Core.getInstance().getTeamManager();

        if(!starting()) return;

        if(team.isRunnerTeam(player)) startManhuntRunning();
    }

    /**
     * Force mobs to ignore spectating/scouting players.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTargetScoutOrSpectator(EntityTargetLivingEntityEvent e) {
        if(!(e.getTarget() instanceof Player player))
            return;

        if(Runner.isScouting(player) || Spectator.isSpectating(player))
            e.setCancelled(true);
    }

    /**
     * Prevent player deaths while waiting for the Manhunt to start.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        ManhuntState state = Core.getInstance().getManhuntState();
        if(state == ManhuntState.SCOUTING || state == ManhuntState.WAITING_FOR_TELEPORT)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakWhileDelayed(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if(!isWaitingForDelay() && !starting()) return;

        long secondsLeft = Core.getInstance().getManhuntManager().getDelaySecondsRemaining();
        warnIfApplicable(player, String.format("You must wait {%d seconds} to break blocks",  secondsLeft));
        event.setCancelled(true);
    }

    private boolean starting() {
        ManhuntManager manhunt = Core.getInstance().getManhuntManager();
        return manhunt.getState() == ManhuntState.STARTING;
    }

    private void startBoonCountdown(Player player) {
        Core.getInstance().getBoonManager().startAllCountdowns(player);
    }

    private boolean isWaitingForDelay() {
        return (Core.getInstance().getManhuntState() == ManhuntState.WAITING_FOR_DELAY);
    }

    private boolean isHunterTeam(Player player) {
        TeamManager team = Core.getInstance().getTeamManager();
        return team.isHunterTeam(player);
    }

    private boolean isRunnerTeam(Player player) {
        TeamManager team = Core.getInstance().getTeamManager();
        return team.isRunnerTeam(player);
    }

    private void warnIfApplicable(Player player, String warning) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (now - lastWarn.getOrDefault(uuid, 0L) > MESSAGE_COOLDOWN) {
            player.playSound(UI.FAIL_SOUND);
            player.sendMessage(SystemMessage.warn(warning));
            lastWarn.put(uuid, now);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastWarn.remove(event.getPlayer().getUniqueId());
    }
}
