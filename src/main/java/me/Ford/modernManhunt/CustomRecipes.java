package me.Ford.modernManhunt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public final class CustomRecipes {

    public static void register() {

        /// GOLDEN HEAD ///

        ItemStack goldenHead = MMFunctions.GoldenHead();

        // Outline the recipe shape
        ShapedRecipe gHeadRecipe = new ShapedRecipe(Keys.GOLDEN_HEAD_RECIPE, goldenHead);
        gHeadRecipe.shape(
                "GGG",
                "GHG",
                "GGG"
        );

        // Now just substitute the ingredients
        gHeadRecipe.setIngredient('G', Material.GOLD_INGOT);
        gHeadRecipe.setIngredient('H', Material.PLAYER_HEAD);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.GOLDEN_HEAD_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.GOLDEN_HEAD_RECIPE);
        }
        Bukkit.addRecipe(gHeadRecipe);

        /// FIERY PICKAXE ///

        ItemStack primePick = MMFunctions.PrimedPickaxe();

        // Outline the recipe shape
        ShapedRecipe pPickRecipe = new ShapedRecipe(Keys.PRIMED_PICK_RECIPE, primePick);
        pPickRecipe.shape(
                "III",
                "CSC",
                " S "
        );

        // Now just substitute the ingredients
        pPickRecipe.setIngredient('I', Material.RAW_IRON);
        pPickRecipe.setIngredient('C', Material.COAL);
        pPickRecipe.setIngredient('S', Material.STICK);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.PRIMED_PICK_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.PRIMED_PICK_RECIPE);
        }
        Bukkit.addRecipe(pPickRecipe);

        /// STRENGTHENED SWORD ///

        ItemStack strengthSword = MMFunctions.StrengthenedSword();

        // Outline the recipe shape
        ShapedRecipe sSwordRecipe = new ShapedRecipe(Keys.STRENGTH_SWORD_RECIPE, strengthSword);
        sSwordRecipe.shape(
                "CCC",
                "CIC",
                "CCC"
        );

        // Now just substitute the ingredients
        sSwordRecipe.setIngredient('I', Material.IRON_SWORD);
        sSwordRecipe.setIngredient('C', Material.COPPER_INGOT);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.STRENGTH_SWORD_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.STRENGTH_SWORD_RECIPE);
        }
        Bukkit.addRecipe(sSwordRecipe);

        /// Bolstered Bow ///

        ItemStack bolsterBow = MMFunctions.BolsteredBow();

        // Outline the recipe shape
        ShapedRecipe bBowRecipe = new ShapedRecipe(Keys.BOLSTER_BOW_RECIPE, bolsterBow);
        bBowRecipe.shape(
                "CCC",
                "CBC",
                "CCC"
        );

        // Now just substitute the ingredients
        bBowRecipe.setIngredient('B', Material.BOW);
        bBowRecipe.setIngredient('C', Material.COPPER_INGOT);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.BOLSTER_BOW_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.BOLSTER_BOW_RECIPE);
        }
        Bukkit.addRecipe(bBowRecipe);

        /// Bundled Arrows ///

        // Create bundled arrows ItemStack
        ItemStack bundledArrows = new ItemStack(Material.ARROW);
        bundledArrows.setAmount(20);

        // Outline the recipe shape
        ShapedRecipe bArrowsRecipe = new ShapedRecipe(Keys.BUNDLED_ARROWS_RECIPE, bundledArrows);
        bArrowsRecipe.shape(
                "FFF",
                "SSS",
                "EEE"
        );

        // Now just substitute the ingredients
        bArrowsRecipe.setIngredient('F', Material.FLINT);
        bArrowsRecipe.setIngredient('S', Material.STICK);
        bArrowsRecipe.setIngredient('E', Material.FEATHER);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.BUNDLED_ARROWS_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.BUNDLED_ARROWS_RECIPE);
        }
        Bukkit.addRecipe(bArrowsRecipe);

        /// Compact Anvil ///

        // Create compact anvil ItemStack
        ItemStack compactAnvil = new ItemStack(Material.ANVIL);

        // Outline the recipe shape
        ShapedRecipe cAnvilRecipe = new ShapedRecipe(Keys.COMPACT_ANVIL_RECIPE, compactAnvil);
        cAnvilRecipe.shape(
                "III",
                " B ",
                "III"
        );

        // Now just substitute the ingredients
        cAnvilRecipe.setIngredient('I', Material.IRON_INGOT);
        cAnvilRecipe.setIngredient('B', Material.IRON_BLOCK);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.COMPACT_ANVIL_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.COMPACT_ANVIL_RECIPE);
        }
        Bukkit.addRecipe(cAnvilRecipe);

        /// Trident ///

        // Create trident ItemStack
        ItemStack trident = new ItemStack(Material.TRIDENT);

        // Outline the recipe shape
        ShapedRecipe tridentRecipe = new ShapedRecipe(Keys.TRIDENT_RECIPE, trident);
        tridentRecipe.shape(
                " QQ",
                " DQ",
                "D  "
        );

        // Now just substitute the ingredients
        tridentRecipe.setIngredient('Q', Material.QUARTZ);
        tridentRecipe.setIngredient('D', Material.DIAMOND);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.TRIDENT_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.TRIDENT_RECIPE);
        }
        Bukkit.addRecipe(tridentRecipe);

    }

}
