package org.ioprocessing.modernManhunt.miscellaneous.bedbombing;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.damage.DamageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;

import java.util.HashSet;
import java.util.Set;

public class BedBombing implements Listener {
    private final ConfigManager<CoreConfig> coreConfig;

    public BedBombing(Core plugin) {
        this.coreConfig = plugin.getCoreConfig();
    }

    // Data tracking for custom bed bomb logic
    private Location explosionLoc = null;
    private final Set<Location> activeBedExplosions = new HashSet<>();

    @EventHandler
    public void onBedClick(PlayerInteractEvent e) {
        CoreConfig.BedBombingAdjustments bedBombing = coreConfig.getConfig().getBedBombingAdjustments();

        if(!bedBombing.isEnabled()) return;

        if(!e.getAction().isRightClick()) return;

        if(e.getClickedBlock() == null) return;

        if(!(e.getClickedBlock().getBlockData() instanceof Bed bedData)) return;

        World.Environment environment = e.getPlayer().getWorld().getEnvironment();

        if (!(environment == World.Environment.NETHER || environment == World.Environment.THE_END)) return;

        // Cancel the normal interaction then set the bed to air
        e.setCancelled(true);
        Block bed = e.getClickedBlock();

        if(bedData.getPart() == Bed.Part.FOOT) {
            BlockFace facing = bedData.getFacing();
            bed.getRelative(facing).setType(Material.AIR);
        } else bed.setType(Material.AIR);

        // Register the explosion location to check for later, then create the explosion
        explosionLoc = e.getClickedBlock().getLocation();
        activeBedExplosions.add(explosionLoc);
        bed.getWorld().createExplosion(explosionLoc, bedBombing.getExplosionStrength(), true, true, null);
        // After a tick, set bedExploded back to false to avoid false positives
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            activeBedExplosions.remove(explosionLoc);
        }, 1L);
    }

    @EventHandler
    public void onBedExplosion(EntityExplodeEvent event) {
        CoreConfig.BedBombingAdjustments bedBombing = coreConfig.getConfig().getBedBombingAdjustments();

        if(!bedBombing.isEnabled()) return;

        if(activeBedExplosions.isEmpty()) return;

        // If the explosion occurs at the location the bed just detonated at, nerf the explosive yields
        if (activeBedExplosions.contains(event.getLocation())) {
            event.setYield(1f/((float) bedBombing.getExplosionStrength()));
        }
    }

    @EventHandler
    public void onExplosionDeath(PlayerDeathEvent e) {
        CoreConfig.BedBombingAdjustments bedBombing = coreConfig.getConfig().getBedBombingAdjustments();

        if(!bedBombing.isEnabled())
            return;

        // If the explosion was caused by a bed, replace the death message to simulate in-game behavior
        if (e.getDamageSource().getDamageType().equals(DamageType.EXPLOSION) && !activeBedExplosions.isEmpty()) {
            String miniMsg = e.getPlayer().getName() + "was killed by <hover:show_text:'MCPE-28723'><click:open_url:'https://bugs.mojang.com/browse/MCPE-28723'>[Intentional Game Design]</click></hover>";
            Component message = MiniMessage.miniMessage().deserialize(miniMsg);
            e.deathMessage(message);
        }
    }
}
