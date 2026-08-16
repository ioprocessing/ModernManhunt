package org.ioprocessing.modernManhunt.miscellaneous.dimensions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.CoreConfig;

public class DimensionalRespawn implements Listener {

    private final NamespacedKey MANHUNT_OVERWORLD = new NamespacedKey("minecraft", "overworld");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn() || event.isAnchorSpawn()) return;

        boolean inManhuntWorld = event.getPlayer().getWorld().getKey().getNamespace()
                .equals(Core.MANHUNT_NAMESPACE);

        CoreConfig.ManhuntSpawnWorld manhuntSpawnWorld = Core.getInstance()
                .getCoreConfig()
                .getConfig()
                .getManhuntSpawnWorld();

        if (inManhuntWorld == manhuntSpawnWorld.isEnabled()) return;

        World overworld;
        if (inManhuntWorld) {
            overworld = Bukkit.getWorld(Core.MANHUNT_OVERWORLD);
        } else {
            String namespace = manhuntSpawnWorld.getOverrideDefaultWorldNamespace();
            String name = manhuntSpawnWorld.getOverrideDefaultWorldName();
            NamespacedKey defaultOverworld = new NamespacedKey(namespace, name);

            overworld = Bukkit.getWorld(defaultOverworld);
        }

        if (overworld == null) return;

        Location spawn = overworld.getSpawnLocation();
        event.setRespawnLocation((isSafeSpawn(spawn) ? spawn : findSafeSpawn(spawn)).add(0.5, 0, 0.5));
    }

    /**
     * Check if a location is safe to spawn at (two passable
     * blocks for the player's body, and a solid block below)
     */
    private boolean isSafeSpawn(Location loc) {
        Block feet = loc.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Block ground = feet.getRelative(BlockFace.DOWN);

        return feet.isPassable() && !feet.isLiquid()
                && head.isPassable() && !head.isLiquid()
                && ground.getType().isSolid();
    }

    /**
     * Spiral outward from the spawn location to find a safe spawn,
     * falling back to the original spawn if none is found
     */
    private Location findSafeSpawn(Location origin) {
        int maxRadius = 10;

        for (int radius = 1; radius <= maxRadius; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;

                    Location candidate = origin.clone().add(dx, 0, dz);

                    for (int dy = -3; dy <= 3; dy++) {
                        Location check = candidate.clone().add(0, dy, 0);
                        if (isSafeSpawn(check)) {
                            check.setYaw(origin.getYaw());
                            check.setPitch(origin.getPitch());
                            return check;
                        }
                    }
                }
            }
        }

        return origin;
    }
}
