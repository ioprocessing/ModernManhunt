package me.Ford.modernManhunt.PlayerListeners;

import me.Ford.modernManhunt.Config;
import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.ModernManhunt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;

public class RunnerListener implements Listener {

    @EventHandler
    public void onRunnerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (!Functions.runnerArray.contains(p))
            return;
        if (p.getKiller() != null) {
            // If a runner kills a runner, prevent a head from dropping
            if (!(Functions.runnerArray.contains(p.getKiller()) && Functions.runnerArray.contains(p)) && Config.headDroppingEnabled)
                e.getDrops().add(CustomItems.consumablePlayerHead(p));
        }
        if (p.hasMetadata("BeingHunted")) {
            boolean aliveRunner = false;
            p.removeMetadata("BeingHunted", ModernManhunt.getInstance());
            p.setMetadata("DeadRunner", new FixedMetadataValue(ModernManhunt.getInstance(), true));
            // If any of the players marked as runners are alive, don't end the game
            for (Player runner : Functions.runnerArray) {
                if (runner.hasMetadata("BeingHunted"))
                    aliveRunner = true;
            }
            if (!aliveRunner) {
                Functions.gameEnd("Hunters win!", NamedTextColor.RED);
                return;
            }
            if (Config.infectionEnabled) {
                // If infection mode is enabled, add them to the hunter team and remove them from the runner team
                Functions.mmHunters.addEntity(p);
                Functions.hunterArray.add(p);
                Functions.runnerArray.remove(p);
            }
        }
    }

    @EventHandler
    public void onRunnerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        // If the respawning player is a runner who died, put them in spectator mode and give them the compass
        if (p.hasMetadata("DeadRunner")) {
            if (!Config.infectionEnabled) {
                Functions.enterSpectator(p);
                Bukkit.getScheduler().runTaskLater(ModernManhunt.getInstance(), () -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 0, false, false, false));
                }, 1L);
                p.give(CustomItems.spectatorCompass());
            }
            else {
                // If the respawning player was a runner that was just killed, show them that they're now a hunter
                Component mainTitle = Component.text("You're now a hunter!", NamedTextColor.RED).decoration(TextDecoration.BOLD, true);
                Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
                Title title = Title.title(mainTitle, Component.empty(), times);
                p.showTitle(title);
                p.removeMetadata("DeadRunner", ModernManhunt.getInstance());
            }
        }
    }

    @EventHandler
    public void onRunnerHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player victim) || !(e.getDamager() instanceof Player attacker))
            return;
        if (!(victim.hasMetadata("PVPImmune") || attacker.hasMetadata("PVPImmune")))
            return;
        // If the runner is hitting someone while immune, remove their immunity
        if (attacker.hasMetadata("PVPImmune")) {
            attacker.removeMetadata("PVPImmune", ModernManhunt.getInstance());
            return;
        }
        // If someone else is hitting the runner while immune, cancel the damage
        e.setCancelled(true);
    }
}
