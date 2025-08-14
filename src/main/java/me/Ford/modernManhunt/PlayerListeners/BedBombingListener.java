package me.Ford.modernManhunt.PlayerListeners;

import me.Ford.modernManhunt.Config;
import me.Ford.modernManhunt.ModernManhunt;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.damage.DamageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BedBombingListener implements Listener {

    // Data tracking for custom bed bomb logic
    Location explosionLoc = null;
    boolean bedExploded = false;
    int explosionPower = Config.bedExplosionStrength;

    @EventHandler
    public void onBedClick(PlayerInteractEvent e) {
        // If the player right-clicked on a bed, and they're in the end or the nether,
        if (e.getAction().isRightClick() && e.getClickedBlock() != null && e.getClickedBlock().getBlockData() instanceof Bed && ((e.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER) || (e.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END))) {
            // Cancel the normal interaction then set the bed to air
            e.setCancelled(true);
            bedExploded = true;
            Block bed = e.getClickedBlock();
            bed.setType(Material.AIR);
            // Register the explosion location to check for later, then create the explosion
            explosionLoc = e.getClickedBlock().getLocation();
            bed.getWorld().createExplosion(explosionLoc, explosionPower, true, true, null);
            // After a tick, set bedExploded back to false to avoid false positives
            Bukkit.getScheduler().runTaskLater(ModernManhunt.getInstance(), () -> {
                bedExploded = false;
            }, 1L);
        }
    }

    @EventHandler
    public void onBedExplosion(EntityExplodeEvent event) {
        // If the explosion occurs at the location the bed just detonated at, nerf the explosive yields
        if ((explosionLoc != null) && (event.getLocation().equals(explosionLoc))) {
            event.setYield(1f/((float) explosionPower));
        }
    }

    @EventHandler
    public void onExplosionDeath(PlayerDeathEvent e) {
        // If the explosion was caused by a bed, replace the death message to simulate in-game behavior
        if (e.getDamageSource().getDamageType().equals(DamageType.EXPLOSION) && bedExploded)
            e.deathMessage(Component.text()
                    .append(Component.text(e.getPlayer().getName()))
                    .append(Component.text(" was killed by [Intentional Game Design]"))
                    .build());
    }
}
