package org.ioprocessing.modernManhunt.handicaps;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum HandicapType {
    TEAMMATE_TP("Teammate TP Handicap", Material.ENDER_PEARL),
    KEEP_ARMOR("Keep Armor Handicap", Material.IRON_CHESTPLATE),
    KEEP_HOTBAR("Keep Hotbar Handicap", Material.BUNDLE),
    KEEP_INVENTORY("Keep Inventory Handicap", Material.CHEST),
    EXACT_COORDS("Exact Coordinates Handicap", Material.RECOVERY_COMPASS),
    FORTRESS_COMPASS("Nether Fortress Compass Handicap", Material.COMPASS);

    private final String label;
    private final Material material;

    HandicapType(String label, Material material) {
        this.label = label;
        this.material = material;
    }
}
