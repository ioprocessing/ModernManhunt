package me.Ford.modernManhunt.PlayerListeners;

import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.ModernManhunt;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class RunnerListener implements Listener {

    @EventHandler
    public void onRunnerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if (p.hasMetadata("BeingHunted")) {
            boolean aliveRunner = false;
            p.removeMetadata("BeingHunted", ModernManhunt.getInstance());
            p.setMetadata("DeadRunner", new FixedMetadataValue(ModernManhunt.getInstance(), true));
            // If any of the players marked as runners are alive, don't end the game
            for (Player runner : ManhuntCommand.runnerArray) {
                if (runner.hasMetadata("BeingHunted"))
                    aliveRunner = true;
            }
            if (!aliveRunner) {
                Functions.gameEnd("Hunters win!", NamedTextColor.RED);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasMetadata("DeadRunner")) {
                        Functions.exitSpectator(onlinePlayer);
                    }
                }
            }
        }
    }
}
