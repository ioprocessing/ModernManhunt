package me.Ford.modernManhunt;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class RecipeGUI {

    public static Inventory PrimedPickaxeRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Primed Pickaxe"));
        inventory.setContents(Collections.PRIMED_PICKAXE_INGREDIENTS);

        return inventory;
    }

    public static Inventory StrengthenedSwordRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Strengthened Sword"));
        inventory.setContents(Collections.STRENGTHENED_SWORD_INGREDIENTS);

        return inventory;
    }

    public static Inventory BolsteredBowRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Bolstered Bow"));
        inventory.setContents(Collections.BOLSTERED_BOW_INGREDIENTS);

        return inventory;
    }

    public static Inventory TridentRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Trident"));
        inventory.setContents(Collections.TRIDENT_INGREDIENTS);

        return inventory;
    }

    public static Inventory CompactAnvilRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Compact Anvil"));
        inventory.setContents(Collections.COMPACT_ANVIL_INGREDIENTS);

        return inventory;
    }

    public static Inventory BundledArrowsRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Bundle of Arrows"));
        inventory.setContents(Collections.BUNDLED_ARROWS_INGREDIENTS);

        return inventory;
    }

    public static Inventory GoldenHeadRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Golden Head"));
        inventory.setContents(Collections.GOLDEN_HEAD_INGREDIENTS);

        return inventory;
    }

    public static Inventory LoyaltyRecipe(Player p) {
        Inventory inventory = Bukkit.createInventory(p, InventoryType.WORKBENCH, Component.text("Loyalty Book (Shapeless)"));
        inventory.setContents(Collections.LOYALTY_INGREDIENTS);

        return inventory;
    }
}
