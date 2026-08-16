package org.ioprocessing.modernManhunt.gamestate;

import org.bukkit.*;
import org.ioprocessing.modernManhunt.Core;

import java.util.ArrayList;
import java.util.List;

public class ScoutParticlesTask {
    private Integer taskId;

    public ScoutParticlesTask() {
        this.taskId = null;
    }

    public void startScoutingParticles(World world) {
        Location spawn = world.getSpawnLocation();

        int centerX = spawn.getBlockX();
        int centerZ = spawn.getBlockZ();

        int minHeight = world.getMinHeight();
        int maxHeight = world.getMaxHeight();

        int radius = Core.getInstance().getCoreConfig().getConfig().getScoutingAllowedChunkRoamDistance() * 16;

        List<int[]> borderPoints = new ArrayList<>();

        for (int x = -radius; x <= radius; x += 4) {
            borderPoints.add(new int[]{x, -radius});
            borderPoints.add(new int[]{x, radius});
        }

        for (int z = -radius; z <= radius; z += 4) {
            borderPoints.add(new int[]{-radius, z});
            borderPoints.add(new int[]{radius, z});
        }

        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.getInstance(), () -> {
            // Spawn location particles
            for(int y = minHeight; y < maxHeight; y += 4) {
                spawnCenterParticle(world, centerX, y, centerZ);
            }

            // Border particles
            for (int y = minHeight; y < maxHeight; y += 4) {
                for (int[] point : borderPoints) {
                    spawnBorderParticle(world,
                            centerX + point[0],
                            y,
                            centerZ + point[1]);
                }
            }
        }, 0L ,10L);
    }

    public void stopScoutingParticles() {
        if(this.taskId != null)
            Bukkit.getScheduler().cancelTask(taskId);
        else Core.logger().warn("Tried to cancel null Scouting Particles task");
    }

    private void spawnCenterParticle(World world, float x, float y, float z) {
        Particle.DustOptions dustColor = new Particle.DustOptions(Color.fromRGB(85, 255, 255), 2.0F);

        world.spawnParticle(Particle.DUST, x, y, z, 4, 0.125, 0.125, 0.125, 0.1, dustColor);
    }

    private void spawnBorderParticle(World world, float x, float y, float z) {
        Particle.DustOptions dustColor = new Particle.DustOptions(Color.fromRGB(255, 255, 85), 2.0f);

        world.spawnParticle(Particle.DUST, x, y, z, 4, 0.25, 0.25, 0.25, 0.1, dustColor);
    }
}
