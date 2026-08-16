package org.ioprocessing.modernManhunt.teams;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManhuntTeam {

    private final Team scoreboardTeam;
    private final List<UUID> players = new ArrayList<>();
    private final TeamType type;

    public ManhuntTeam(Team scoreboardTeam, TeamType type) {
        this.scoreboardTeam = scoreboardTeam;
        this.type = type;
    }

    public void addPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        if(!players.contains(uuid)) {
            scoreboardTeam.addEntry(player.getName());
        }
        players.add(uuid);
    }

    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();

        if(players.remove(uuid)) {
            scoreboardTeam.removeEntry(player.getName());
        }
    }

    public enum TeamType {
        HUNTERS, RUNNERS, SPECTATORS
    }

    public TeamType getType() { return type; }

    public boolean contains(Player player) {
        return players.contains(player.getUniqueId());
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public Team getScoreboardTeam() {
        return scoreboardTeam;
    }
}
