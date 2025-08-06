package me.Ford.modernManhunt.CustomItems;

import me.Ford.modernManhunt.Config;
import me.Ford.modernManhunt.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public final class CustomRecipes {

    public static void register() {

        Config.getInstance().load();

        /// GOLDEN HEAD ///

        ItemStack goldenHead = CustomItems.goldenHead();

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
        if (Config.getInstance().isRecipeEnabled("Golden Head"))
            Bukkit.addRecipe(gHeadRecipe);

        /// FIERY PICKAXE ///

        ItemStack primePick = CustomItems.primedPickaxe();

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
        if (Config.getInstance().isRecipeEnabled("Primed Pickaxe"))
            Bukkit.addRecipe(pPickRecipe);

        /// STRENGTHENED SWORD ///

        ItemStack strengthSword = CustomItems.strengthenedSword();

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

        if (Config.getInstance().isRecipeEnabled("Strengthened Sword"))
            Bukkit.addRecipe(sSwordRecipe);

        /// Bolstered Bow ///

        ItemStack bolsterBow = CustomItems.bolsteredBow();

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

        if (Config.getInstance().isRecipeEnabled("Bolstered Bow"))
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

        if (Config.getInstance().isRecipeEnabled("Bundled Arrows"))
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

        if (Config.getInstance().isRecipeEnabled("Compact Anvil"))
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

        if (Config.getInstance().isRecipeEnabled("Trident"))
            Bukkit.addRecipe(tridentRecipe);

        /// Loyalty ///

        // Create book ItemStack
        ItemStack book = CustomItems.loyaltyBook();

        // Outline the recipe shape
        ShapelessRecipe loyaltyRecipe = new ShapelessRecipe(Keys.LOYALTY_RECIPE, book);

        // Now just substitute the ingredients
        loyaltyRecipe.addIngredient(1, Material.ENDER_PEARL);
        loyaltyRecipe.addIngredient(1, Material.COMPASS);
        loyaltyRecipe.addIngredient(1, Material.BOOK);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.LOYALTY_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.LOYALTY_RECIPE);
        }

        if (Config.getInstance().isRecipeEnabled("Loyalty Book"))
            Bukkit.addRecipe(loyaltyRecipe);

        ///  Trial Chambers Map ///

        ItemStack dummyMap = CustomItems.dummyTrialMap();

        // Outline the recipe shape
        ShapedRecipe trialMapRecipe = new ShapedRecipe(Keys.TRIAL_MAP_RECIPE, dummyMap);
        trialMapRecipe.shape(
                " C ",
                "COC",
                " C "
        );

        // Now just substitute the ingredients
        trialMapRecipe.setIngredient('C', Material.COPPER_BLOCK);
        trialMapRecipe.setIngredient('O', Material.COMPASS);

        // Don't duplicate recipe
        if (Bukkit.getRecipe(Keys.TRIAL_MAP_RECIPE) != null) {
            Bukkit.removeRecipe(Keys.TRIAL_MAP_RECIPE);
        }

        if (Config.getInstance().isRecipeEnabled("Trial Chambers Map"))
            Bukkit.addRecipe(trialMapRecipe);

    }

}
