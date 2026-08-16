package org.ioprocessing.modernManhunt.boons;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.BoonConfig;

import java.util.*;

public class BoonManager implements Listener {

    private final Core core;

    private final Map<BoonType, Boon> registry = new EnumMap<>(BoonType.class);

    private final Map<UUID, Map<BoonType, ActiveBoon>> activeBoons = new HashMap<>();
    private final Map<UUID, Map<BoonType, ActiveBoon>> pendingResets = new HashMap<>();
    private final Map<UUID, Map<BoonType, BossBar>> boonBossBars = new HashMap<>();

    // Store tasks so we can cancel them on premature end
    private final Map<UUID, Map<BoonType, BukkitTask>> expiryTasks = new HashMap<>();
    private final Map<UUID, Map<BoonType, BukkitTask>> bossBarTasks = new HashMap<>();

    public BoonManager(Core core) {
        this.core = core;
    }

    /**
     * Register a boon implementation during plugin startup.
     */
    public void register(Boon boon) {
        registry.put(boon.getType(), boon);
    }

    /**
     * Clears the registry and re-registers all boons.
     */
    public void reloadRegistry() {
        registry.clear();
        Core.getInstance().registerBoons();
    }

    public Map<BoonType, Boon> getRegistry() {
        return Collections.unmodifiableMap(registry);
    }

    /**
     * Apply a boon to a player. Safe to call even if the boon is already active.
     */
    public void applyBoon(Player player, BoonType type) {
        Boon boon = registry.get(type);
        if (boon == null) {
            Core.logger().warn("Tried to apply unregistered boon: {}", type);
            return;
        }

        if (!boon.isEnabled()) {
            Core.logger().warn("Tried to apply disabled boon: {}", type);
            return;
        }

        // If already active, reset cleanly before reapplying
        UUID uuid = player.getUniqueId();
        cancelBoon(player, type);

        ActiveBoon activeBoon = new ActiveBoon(type);
        boon.apply(player, activeBoon.getSavedState());

        activeBoons
                .computeIfAbsent(uuid, id -> new EnumMap<>(BoonType.class))
                .put(type, activeBoon);
    }

    /**
     * Start the expiration countdown for an already-active boon.
     * Does nothing if the boon is not currently active on the player.
     * Shows a boss-bar for the boon duration if enabled.
     */
    public void startCountdown(Player player, BoonType type) {
        UUID uuid = player.getUniqueId();
        if (!hasBoon(player, type)) return;

        Boon boon = registry.get(type);
        if (boon == null) return;

        if (boon.isShowBossBar()) {
            BossBar bar = BossBar.bossBar(
                    Component.text(boon.getType().getLabel()),
                    1.0f,
                    BossBar.Color.GREEN,
                    BossBar.Overlay.PROGRESS
            );

            player.showBossBar(bar);

            boonBossBars
                    .computeIfAbsent(uuid, id -> new EnumMap<>(BoonType.class))
                    .put(type, bar);

            long durationTicks = boon.getDurationTicks();
            long[] ticksElapsed = {0};

            BukkitTask[] barTask = {null};

            barTask[0] = Bukkit.getScheduler().runTaskTimer(core, () -> {
                ticksElapsed[0]++;
                float progress = 1.0f - ((float) ticksElapsed[0] / durationTicks);

                if (progress <= 0 || !hasBoon(player, type)) {
                    player.hideBossBar(bar);
                    boonBossBars.getOrDefault(uuid, Collections.emptyMap()).remove(type);
                    if (barTask[0] != null) barTask[0].cancel();
                    return;
                }

                bar.progress(Math.max(0f, progress));
            }, 0L, 1L);

            bossBarTasks
                    .computeIfAbsent(uuid, id -> new EnumMap<>(BoonType.class))
                    .put(type, barTask[0]);
        }

        BukkitTask expiryTask = Bukkit.getScheduler().runTaskLater(
                core, () -> expireBoon(uuid, type), boon.getDurationTicks()
        );

        expiryTasks
                .computeIfAbsent(uuid, id -> new EnumMap<>(BoonType.class))
                .put(type, expiryTask);
    }

    /**
     * Start the expiration countdown for all currently active boons on a player.
     */
    public void startAllCountdowns(Player player) {
        Set<BoonType> types = new HashSet<>(
                activeBoons.getOrDefault(player.getUniqueId(), Collections.emptyMap()).keySet()
        );
        for (BoonType type : types) {
            startCountdown(player, type);
        }
    }

    /**
     * Immediately cancel and reset a specific boon on a player.
     */
    public void cancelBoon(Player player, BoonType type) {
        UUID uuid = player.getUniqueId();
        Map<BoonType, ActiveBoon> playerBoons = activeBoons.get(uuid);
        if (playerBoons == null) return;

        ActiveBoon activeBoon = playerBoons.remove(type);
        if (activeBoon == null) return;

        Boon boon = registry.get(type);
        if (boon != null) boon.reset(player, activeBoon.getSavedState());

        // Cancel expiry task
        Map<BoonType, BukkitTask> expiry = expiryTasks.get(uuid);
        if (expiry != null) {
            BukkitTask task = expiry.remove(type);
            if (task != null) task.cancel();
        }

        // Cancel boss bar task
        Map<BoonType, BukkitTask> barTasks = bossBarTasks.get(uuid);
        if (barTasks != null) {
            BukkitTask task = barTasks.remove(type);
            if (task != null) task.cancel();
        }

        // Hide boss bar
        Map<BoonType, BossBar> bars = boonBossBars.get(uuid);
        if (bars != null) {
            BossBar bar = bars.remove(type);
            if (bar != null) player.hideBossBar(bar);
        }
    }

    /**
     * Cancel all active boons on a player (e.g. on game end or player death).
     */
    public void cancelAllBoons(Player player) {
        Set<BoonType> types = new HashSet<>(
                activeBoons.getOrDefault(player.getUniqueId(), Collections.emptyMap()).keySet()
        );
        for (BoonType type : types) {
            cancelBoon(player, type);
        }
    }

    public boolean hasBoon(Player player, BoonType type) {
        return activeBoons
                .getOrDefault(player.getUniqueId(), Collections.emptyMap())
                .containsKey(type);
    }

    public void applyApplicableBoons(Player player) {
        BoonConfig boonConfig = core.getBoon().getConfig();

        for (BoonType type : registry.keySet()) {
            if (boonConfig.isApplicable(player, type)) {
                applyBoon(player, type);
            }
        }
    }

    /// INTERNAL

    private void expireBoon(UUID uuid, BoonType type) {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            // If player is offline, move to pending so we reset on rejoin
            Map<BoonType, ActiveBoon> playerBoons = activeBoons.get(uuid);
            if (playerBoons == null) return;

            ActiveBoon activeBoon = playerBoons.remove(type);
            if (activeBoon == null) return;

            pendingResets
                    .computeIfAbsent(uuid, id -> new EnumMap<>(BoonType.class))
                    .put(type, activeBoon);
            return;
        }

        cancelBoon(player, type);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Map<BoonType, ActiveBoon> pending = pendingResets.remove(uuid);
        if (pending == null) return;

        for (Map.Entry<BoonType, ActiveBoon> entry : pending.entrySet()) {
            Boon boon = registry.get(entry.getKey());
            if (boon != null) boon.reset(player, entry.getValue().getSavedState());
        }
    }

    /**
     * Check whether a boon is registered and enabled.
     */
    public boolean isEnabled(BoonType type) {
        Boon boon = registry.get(type);
        return boon != null && boon.isEnabled();
    }
}