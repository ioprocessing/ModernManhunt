package org.ioprocessing.modernManhunt.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for Piglin bartering loot tables
 */
@Setter
@Getter
@ConfigSerializable
public class BarterPoolConfig {
    private boolean isEnabled = true;

    private List<LootEntry> barteringPool = createDefaultBarteringPool();

    private static List<LootEntry> createDefaultBarteringPool() {
        List<LootEntry> loot = new ArrayList<>();

        // Soul speed book
        loot.add(new LootEntry(
                Material.ENCHANTED_BOOK,
                5,
                1,
                1,
                "SOUL_SPEED",
                1,
                3));

        // Soul speed boots
        loot.add(new LootEntry(
                Material.IRON_BOOTS,
                8,
                1,
                1,
                "SOUL_SPEED",
                1,
                3));

        // Regular fire resistance
        loot.add(new LootEntry(
                Material.POTION,
                10,
                1,
                1,
                PotionType.FIRE_RESISTANCE));

        // Splash fire resistance
        loot.add(new LootEntry(
                Material.SPLASH_POTION,
                10,
                1,
                1,
                PotionType.FIRE_RESISTANCE));

        loot.add(new LootEntry(Material.IRON_NUGGET, 10, 9, 36));

        loot.add(new LootEntry(Material.QUARTZ, 20, 8, 16));
        loot.add(new LootEntry(Material.GLOWSTONE_DUST, 20, 5, 12));
        loot.add(new LootEntry(Material.LEATHER, 20, 4, 10));
        loot.add(new LootEntry(Material.ENDER_PEARL, 20, 4, 8));
        loot.add(new LootEntry(Material.STRING, 20, 8, 24));

        loot.add(new LootEntry(Material.FIRE_CHARGE, 40, 1, 5));
        loot.add(new LootEntry(Material.GRAVEL, 40, 8, 16));
        loot.add(new LootEntry(Material.SPECTRAL_ARROW, 40, 6, 12));
        loot.add(new LootEntry(Material.NETHER_BRICK, 40, 4, 16));
        loot.add(new LootEntry(Material.OBSIDIAN, 40, 1, 1));
        loot.add(new LootEntry(Material.CRYING_OBSIDIAN, 40, 1, 3));

        // Not-included 1.21.11 loot table entries
        loot.add(new LootEntry(Material.DRIED_GHAST, 0, 1, 1));
        loot.add(new LootEntry(Material.BLACKSTONE, 0, 8, 16));
        loot.add(new LootEntry(
                Material.POTION,
                0,
                1,
                1,
                PotionType.WATER));

        return loot;
    }

    /**
     * An item entry that can be added to the bartering pool
     */
    @ConfigSerializable
    @Getter
    @Setter
    public static class LootEntry {

        private Material material = Material.STONE;

        private int weight = 10;

        private int minQuantity = 1;

        private int maxQuantity = 1;

        private String enchantment;

        private Integer minEnchantmentLevel;

        private Integer maxEnchantmentLevel;

        private PotionType potionType;

        public LootEntry() {}

        /**
         * Construct a loot table item to add to the custom Piglin bartering pool
         *
         * @param material Base item material for the loot table entry
         * @param weight The weight of the item, where chance to obtain = item weight / total loot table weight
         * @param minQuantity Minimum amount the stack size can be when item is generated
         * @param maxQuantity Maximum amount the stack size can be when item is generated
         */
        public LootEntry(
                Material material,
                int weight,
                int minQuantity,
                int maxQuantity) {
            this.material = material;
            this.weight = weight;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
        }

        /**
         * Construct an enchanted loot table item to add to the custom Piglin bartering pool
         *
         * @param material Base item material for the loot table entry
         * @param weight The weight of the item, where chance to obtain = item weight / total loot table weight
         * @param minQuantity Minimum amount the stack size can be when item is generated
         * @param maxQuantity Maximum amount the stack size can be when item is generated
         * @param enchantment What enchantment the item should have
         * @param minEnchantmentLevel Minimum level that the enchantment can be
         * @param maxEnchantmentLevel Maximum level that the enchantment can be
         */
        public LootEntry(
                Material material,
                int weight,
                int minQuantity,
                int maxQuantity,
                String enchantment,
                int minEnchantmentLevel,
                int maxEnchantmentLevel) {
            this.material = material;
            this.weight = weight;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
            this.enchantment = enchantment;
            this.minEnchantmentLevel = minEnchantmentLevel;
            this.maxEnchantmentLevel = maxEnchantmentLevel;
        }

        /**
         * Construct a loot table potion to add to the custom Piglin bartering pool
         *
         * @param material Base item material for the loot table entry
         * @param weight The weight of the item, where chance to obtain = item weight / total loot table weight
         * @param minQuantity Minimum amount the stack size can be when item is generated
         * @param maxQuantity Maximum amount the stack size can be when item is generated
         * @param potionType What type of potion it is (if base item is potion)
         */
        public LootEntry(
                Material material,
                int weight,
                int minQuantity,
                int maxQuantity,
                PotionType potionType) {
            this.material = material;
            this.weight = weight;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
            this.potionType = potionType;
        }
    }
}
