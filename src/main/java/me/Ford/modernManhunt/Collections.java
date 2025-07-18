package me.Ford.modernManhunt;

import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Records.BarteringDrops;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.Ford.modernManhunt.Functions.random;

public class Collections {
    public static final Map<Material, Material> SMELT = new HashMap<>();
    public static final Map<Material, Float> EXP = new HashMap<>();
    public static final Map<String, ItemStack> RECIPES = new HashMap<>();
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

                RECIPES.put("Primed Pickaxe", CustomItems.primedPickaxe());
                RECIPES.put("Strengthened Sword", CustomItems.strengthenedSword());
                RECIPES.put("Bolstered Bow", CustomItems.bolsteredBow());
                RECIPES.put("Trident", new ItemStack(Material.TRIDENT));
                RECIPES.put("Compact Anvil", CustomItems.compactAnvil());
                RECIPES.put("Bundled Arrows", CustomItems.bundledArrows());
                RECIPES.put("Golden Head", CustomItems.goldenHead());
                RECIPES.put("Loyalty Book", CustomItems.loyaltyBook());
            }

    public static final ItemStack[] PRIMED_PICKAXE_INGREDIENTS = {
            CustomItems.primedPickaxe(),
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
            CustomItems.strengthenedSword(),
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
            CustomItems.bolsteredBow(),
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
            CustomItems.goldenHead(),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            CustomItems.dummyHead(),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.GOLD_INGOT)};

    public static final ItemStack[] LOYALTY_INGREDIENTS = {
            CustomItems.loyaltyBook(),
            new ItemStack(Material.ENDER_PEARL),
            new ItemStack(Material.BOOK),
            null,
            new ItemStack(Material.COMPASS)};

    public static final List<BarteringDrops> CUSTOM_BARTER_POOL = List.of(
            new BarteringDrops(5, () -> {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta esm = (EnchantmentStorageMeta)book.getItemMeta();
                esm.addStoredEnchant(Enchantment.SOUL_SPEED, random(3, 1), false);
                book.setItemMeta(esm);
                return book;
            }),
            new BarteringDrops(8, () -> {
                ItemStack boots = new ItemStack(Material.IRON_BOOTS);
                ItemMeta meta = boots.getItemMeta();
                meta.addEnchant(Enchantment.SOUL_SPEED, random(3, 1), false);
                boots.setItemMeta(meta);
                return boots;
            }),
            new BarteringDrops(10, () -> new ItemStack(Material.IRON_NUGGET, random(36, 9))),
            new BarteringDrops(10, () -> CustomItems.fireResistancePotion(true)),  // Splash
            new BarteringDrops(10, () -> CustomItems.fireResistancePotion(false)), // Regular
            new BarteringDrops(20, () -> new ItemStack(Material.QUARTZ, random(16, 8))),
            new BarteringDrops(20, () -> new ItemStack(Material.GLOWSTONE_DUST, random(12, 5))),
            new BarteringDrops(20, () -> new ItemStack(Material.MAGMA_CREAM, random(6, 2))),
            new BarteringDrops(20, () -> new ItemStack(Material.ENDER_PEARL, random(8, 4))),
            new BarteringDrops(20, () -> new ItemStack(Material.STRING, random(24, 8))),
            new BarteringDrops(40, () -> new ItemStack(Material.FIRE_CHARGE, random(5, 1))),
            new BarteringDrops(40, () -> new ItemStack(Material.GRAVEL, random(16, 8))),
            new BarteringDrops(40, () -> new ItemStack(Material.LEATHER, random(10, 4))),
            new BarteringDrops(40, () -> new ItemStack(Material.NETHER_BRICK, random(16, 4))),
            new BarteringDrops(40, () -> new ItemStack(Material.OBSIDIAN, 1)),
            new BarteringDrops(40, () -> new ItemStack(Material.CRYING_OBSIDIAN, random(3, 1))),
            new BarteringDrops(40, () -> new ItemStack(Material.SOUL_SAND, random(16, 4)))
    );
}
