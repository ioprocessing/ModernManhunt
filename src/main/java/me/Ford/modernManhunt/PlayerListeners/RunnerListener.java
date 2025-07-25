package me.Ford.modernManhunt.PlayerListeners;

import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.ModernManhunt;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RunnerListener implements Listener {

    @EventHandler
    public void onRunnerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (!ManhuntCommand.runnerArray.contains(p))
            return;
        e.getDrops().add(CustomItems.consumablePlayerHead(p));
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

    @EventHandler
    public void onRunnerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        // If the respawning player is a runner who died, put them in spectator mode and give them the compass
        if (p.hasMetadata("DeadRunner")) {
            Functions.enterSpectator(p);
            Bukkit.getScheduler().runTaskLater(ModernManhunt.getInstance(), () -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 0, false, false, false));
            }, 1L);
            p.give(CustomItems.spectatorCompass());
        }
    }
}
