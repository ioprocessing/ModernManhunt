package me.Ford.modernManhunt.OtherListeners;

import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.ModernManhunt;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental

public class EnderDragonKillListener implements Listener {

    @EventHandler
    public void onEnderDragonDeath(EntityDeathEvent event) {
        Entity e = event.getEntity();
        if (e.getType().equals(EntityType.ENDER_DRAGON)) {
            boolean aliveRunner = false;
            // If any of the players marked as runners are alive, end the game
            for (Player runner : ManhuntCommand.runnerArray) {
                if (runner.hasMetadata("BeingHunted"))
                    aliveRunner = true;
            }
            if (aliveRunner) {
                Functions.gameEnd("Runners win!", NamedTextColor.GREEN);
                for (Player runner : ManhuntCommand.runnerArray) {runner.removeMetadata("BeingHunted",  ModernManhunt.getInstance());}
            }
        }
    }
}
