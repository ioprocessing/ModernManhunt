package me.Ford.modernManhunt.CustomItems;

import me.Ford.modernManhunt.Collections;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class RecipeFunctions {

    public static Inventory primedPickaxeRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Primed Pickaxe"));
        inventory.setContents(Collections.PRIMED_PICKAXE_INGREDIENTS);

        return inventory;
    }

    public static Inventory strengthenedSwordRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Strengthened Sword"));
        inventory.setContents(Collections.STRENGTHENED_SWORD_INGREDIENTS);

        return inventory;
    }

    public static Inventory bolsteredBowRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Bolstered Bow"));
        inventory.setContents(Collections.BOLSTERED_BOW_INGREDIENTS);

        return inventory;
    }

    public static Inventory tridentRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Trident"));
        inventory.setContents(Collections.TRIDENT_INGREDIENTS);

        return inventory;
    }

    public static Inventory compactAnvilRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Compact Anvil"));
        inventory.setContents(Collections.COMPACT_ANVIL_INGREDIENTS);

        return inventory;
    }

    public static Inventory bundledArrowsRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Bundle of Arrows"));
        inventory.setContents(Collections.BUNDLED_ARROWS_INGREDIENTS);

        return inventory;
    }

    public static Inventory goldenHeadRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Golden Head"));
        inventory.setContents(Collections.GOLDEN_HEAD_INGREDIENTS);

        return inventory;
    }

    public static Inventory loyaltyRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Loyalty Book (Shapeless)"));
        inventory.setContents(Collections.LOYALTY_INGREDIENTS);

        return inventory;
    }
}
