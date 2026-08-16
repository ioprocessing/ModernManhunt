package org.ioprocessing.modernManhunt.boons;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum BoonType {
    SIZE("Size Boon", Material.TADPOLE_BUCKET),
    SPEED("Speed Boon", Material.IRON_BOOTS),
    HEAD("Starting Head Boon", Material.PLAYER_HEAD);

    private final String label;
    private final Material material;

    BoonType(String label, Material material) {
        this.label = label;
        this.material = material;
    }
}
