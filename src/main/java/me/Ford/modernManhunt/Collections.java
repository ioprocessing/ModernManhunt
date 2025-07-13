package me.Ford.modernManhunt;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Collections {
    public static final Map<Material, Material> SMELT = new HashMap<>();
    public static final Map<Material, Float> EXP = new HashMap<>();
            static {
                SMELT.put(Material.IRON_ORE, Material.IRON_INGOT);
                SMELT.put(Material.COPPER_ORE, Material.COPPER_INGOT);
                SMELT.put(Material.GOLD_ORE, Material.GOLD_INGOT);
                SMELT.put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
                SMELT.put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
                SMELT.put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
                SMELT.put(Material.RAW_IRON_BLOCK, Material.IRON_BLOCK);
                SMELT.put(Material.RAW_COPPER_BLOCK, Material.COPPER_BLOCK);
                SMELT.put(Material.RAW_GOLD_BLOCK, Material.GOLD_BLOCK);
                SMELT.put(Material.SAND, Material.GLASS);
                SMELT.put(Material.RED_SAND, Material.GLASS);
                SMELT.put(Material.WET_SPONGE, Material.SPONGE);

                EXP.put(Material.IRON_INGOT, 0.7f);
                EXP.put(Material.COPPER_INGOT, 0.7f);
                EXP.put(Material.GOLD_INGOT, 1.0f);
                EXP.put(Material.IRON_BLOCK, 0.7f);
                EXP.put(Material.COPPER_BLOCK, 0.7f);
                EXP.put(Material.GOLD_BLOCK, 1.0f);
                EXP.put(Material.GLASS, 0.1f);
                EXP.put(Material.SPONGE, 0.15f);
            }

    public static final ItemStack[] PRIMED_PICKAXE_INGREDIENTS = {
            MMFunctions.PrimedPickaxe(),
            new ItemStack(Material.RAW_IRON),
            new ItemStack(Material.RAW_IRON),
            new ItemStack(Material.RAW_IRON),
            new ItemStack(Material.COAL),
            new ItemStack(Material.STICK),
            new ItemStack(Material.COAL),
            null,
            new ItemStack(Material.STICK),
            null};

    public static final ItemStack[] STRENGTHENED_SWORD_INGREDIENTS = {
            MMFunctions.StrengthenedSword(),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.IRON_SWORD),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT)};

    public static final ItemStack[] BOLSTERED_BOW_INGREDIENTS = {
            MMFunctions.BolsteredBow(),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.BOW),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT),
            new ItemStack(Material.COPPER_INGOT)};

    public static final ItemStack[] TRIDENT_INGREDIENTS = {
            new ItemStack(Material.TRIDENT),
            null,
            new ItemStack(Material.QUARTZ),
            new ItemStack(Material.QUARTZ),
            null,
            new ItemStack(Material.DIAMOND),
            new ItemStack(Material.QUARTZ),
            new ItemStack(Material.DIAMOND),
            null,
            null};

    public static final ItemStack[] COMPACT_ANVIL_INGREDIENTS = {
            new ItemStack(Material.ANVIL),
            new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT),
            null,
            new ItemStack(Material.IRON_BLOCK),
            null,
            new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.IRON_INGOT)};

    public static final ItemStack[] BUNDLED_ARROWS_INGREDIENTS = {
            new ItemStack(Material.ARROW, 20),
            new ItemStack(Material.FLINT),
            new ItemStack(Material.FLINT),
            new ItemStack(Material.FLINT),
            new ItemStack(Material.STICK),
            new ItemStack(Material.STICK),
            new ItemStack(Material.STICK),
            new ItemStack(Material.FEATHER),
            new ItemStack(Material.FEATHER),
            new ItemStack(Material.FEATHER)};

    public static final ItemStack[] GOLDEN_HEAD_INGREDIENTS = {
            MMFunctions.GoldenHead(),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            MMFunctions.DummyHead(),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT)};

    public static final ItemStack[] LOYALTY_INGREDIENTS = {
            MMFunctions.loyaltyBook(),
            new ItemStack(Material.ENDER_PEARL),
            new ItemStack(Material.BOOK),
            null,
            new ItemStack(Material.COMPASS)};
}
