package org.ioprocessing.modernManhunt.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;

/**
 * Custom implementation for ender pearl cooldown
 */
public class EnderPearl implements Listener {

    private final ConfigManager<CoreConfig> coreConfig;

    public EnderPearl(Core core) {
        this.coreConfig = core.getCoreConfig();
    }

    @EventHandler (ignoreCancelled = true)
    public void onPlayerPearl(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();

        if(shooter instanceof Player player && event.getEntityType() == EntityType.ENDER_PEARL) {
            // Pearl cooldown is also done internally so we need to delay it by a tick
            Bukkit.getScheduler().runTask(Core.getInstance(), () ->
                    player.setCooldown(Material.ENDER_PEARL,
                            coreConfig
                            .getConfig()
                            .getCooldowns()
                            .getEnderPearlCooldownSeconds() * 20)
            );
        }
    }
}
