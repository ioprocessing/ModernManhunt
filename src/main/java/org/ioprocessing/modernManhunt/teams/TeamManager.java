package org.ioprocessing.modernManhunt.teams;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;
import org.ioprocessing.modernManhunt.items.HunterCompass;

import java.util.*;

public class TeamManager {
    private final ConfigManager<CoreConfig> coreConfig;
    public final Scoreboard board;

    private final ManhuntTeam hunters;
    private final ManhuntTeam runners;
    private final ManhuntTeam spectators;

    private final Map<UUID, ManhuntTeam> playerTeamMap = new HashMap<>();

    public TeamManager(Core plugin) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.board = manager.getNewScoreboard();
        this.coreConfig = plugin.getCoreConfig();

        hunters = new ManhuntTeam(setupTeam(board.registerNewTeam("hunters"), NamedTextColor.RED), ManhuntTeam.TeamType.HUNTERS);
        runners = new ManhuntTeam(setupTeam(board.registerNewTeam("runners"), NamedTextColor.GREEN), ManhuntTeam.TeamType.RUNNERS);
        spectators = new ManhuntTeam(setupSpectatorTeam(), ManhuntTeam.TeamType.SPECTATORS);
    }

    private Team setupTeam(Team team, NamedTextColor color) {
        if(coreConfig.getConfig().isTeamColors())
            team.color(color);
        return team;
    }

    private Team setupSpectatorTeam() {
        Team team = board.registerNewTeam("spectators");
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);
        if(coreConfig.getConfig().isTeamColors())
            team.color(NamedTextColor.GRAY);

        return team;
    }

    /**
     * Gets the runner currently targeted by the hunter's compass
     */
    public Player getCurrentTarget(Player hunter) {
        if(hunter == null) return null;

        List<Player> runners = getRunners();
        if (runners.isEmpty())
            return null;

        Integer index = hunter.getPersistentDataContainer()
                .get(HunterCompass.TARGETED_PLAYER, PersistentDataType.INTEGER);

        if (index == null || index >= runners.size())
            index = 0;

        return runners.get(index);
    }

    /**
     * Team helper methods
     */
    public List<Player> getHunters() {
        List<Player> hunterPlayers = new ArrayList<>();
        for(UUID uuid : hunters.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                hunterPlayers.add(player);
        }
        return hunterPlayers;
    }

    public List<Player> getRunners() {
        List<Player> runnerPlayers = new ArrayList<>();
        for(UUID uuid : runners.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                runnerPlayers.add(player);
        }
        return runnerPlayers;
    }

    public List<Player> getSpectators() {
        List<Player> spectatorPlayers = new ArrayList<>(List.of());
        for(UUID uuid : spectators.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                spectatorPlayers.add(player);
        }
        return spectatorPlayers;
    }

    public long getOnlineRunnerCount() {
        return runners.getPlayers().stream()
                .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .count();
    }

    public ManhuntTeam getHunterTeam() { return hunters; }
    public ManhuntTeam getRunnerTeam() { return runners; }
    public ManhuntTeam getSpectatorTeam() { return spectators; }

    public boolean isHunterTeam(Player player) {
        return hunters.contains(player);
    }
    public boolean isRunnerTeam(Player player) {
        return runners.contains(player);
    }
    public boolean isSpectatorTeam(Player player) {
        return spectators.contains(player);
    }
    public boolean isParticipant(Player player) {
        return (hunters.contains(player) || runners.contains(player));
    }

    public void setHunterTeam(Player player) {
        setOnlyTeam(player, ManhuntTeam.TeamType.HUNTERS);
    }
    public void setRunnerTeam(Player player) {
        setOnlyTeam(player, ManhuntTeam.TeamType.RUNNERS);
    }
    public void setSpectatorTeam(Player player) {
        setOnlyTeam(player, ManhuntTeam.TeamType.SPECTATORS);
    }

    public void removeHunter(Player player) {
        playerTeamMap.remove(player.getUniqueId(), hunters);
        hunters.removePlayer(player);
    }

    public void removeRunner(Player player) {
        playerTeamMap.remove(player.getUniqueId(), runners);
        runners.removePlayer(player);
    }

    public void removeSpectator(Player player) {
        playerTeamMap.remove(player.getUniqueId(), spectators);
        spectators.removePlayer(player);
    }

    public ManhuntTeam getPlayerTeam(Player player) {
        return playerTeamMap.get(player.getUniqueId());
    }

    private void setOnlyTeam(Player player, ManhuntTeam.TeamType type) {
        switch (type) {
            case HUNTERS -> {
                playerTeamMap.put(player.getUniqueId(), hunters);
                runners.removePlayer(player);
                spectators.removePlayer(player);
                hunters.addPlayer(player);
            }
            case RUNNERS -> {
                playerTeamMap.put(player.getUniqueId(), runners);
                hunters.removePlayer(player);
                spectators.removePlayer(player);
                runners.addPlayer(player);
            }
            case SPECTATORS -> {
                playerTeamMap.put(player.getUniqueId(), spectators);
                hunters.removePlayer(player);
                runners.removePlayer(player);
                spectators.addPlayer(player);
            }
        }
    }
}
