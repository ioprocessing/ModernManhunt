package me.Ford.modernManhunt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (p.hasMetadata("OpenedRecipesMenu")) {
            List<MetadataValue> metadata = p.getMetadata("OpenedRecipesMenu");
            if (!metadata.isEmpty() && metadata.getFirst().asString().equals("Recipes Menu")) {
                e.setCancelled(true);

                switch (e.getSlot()) {
                    case 0 -> {
                        p.openInventory(RecipeGUI.PrimedPickaxeRecipe(p));
                        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(), "Primed Pickaxe Recipe"));
                    }
                    case 1 -> {
                        p.openInventory(RecipeGUI.StrengthenedSwordRecipe(p));
                        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(), "Strengthened Sword Recipe"));
                    }
                    case 2 -> {
                        p.openInventory(RecipeGUI.BolsteredBowRecipe(p));
                        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(), "Bolstered Bow Recipe"));
                    }
                    case 3 -> {
                        p.openInventory(RecipeGUI.TridentRecipe(p));
                        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(), "Trident Recipe"));
                    }
                    case 4 -> {
                        p.openInventory(RecipeGUI.CompactAnvilRecipe(p));
                        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(), "Compact Anvil Recipe"));
                    }
                    case 5 -> {
                        p.openInventory(RecipeGUI.BundledArrowsRecipe(p));
                        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(), "Bundle of Arrows Recipe"));
                    }
                    case 6 -> {
                        p.openInventory(RecipeGUI.GoldenHeadRecipe(p));
                        p.setMetadata("OpenedRecipesMenu", new FixedMetadataValue(ModernManhunt.getInstance(), "Golden Head Recipe"));
                    }
                }
            }
        } else if (p.hasMetadata("OpenedSpectatorMenu") || p.hasMetadata("OpenedTPMenu")) {
            e.setCancelled(true);
        if (e.getCurrentItem() == null || !e.getCurrentItem().getType().equals(Material.PLAYER_HEAD))
                return;
            SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
            if (!(p.hasMetadata("OpenedTPMenu") && meta.getOwningPlayer().getPlayer().isDead()))
                p.teleport(meta.getOwningPlayer().getLocation());
            else {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
                p.sendMessage("§cThat player is currently respawning!");
            }
            p.closeInventory();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (p.hasMetadata("OpenedSpectatorMenu"))
            p.removeMetadata("OpenedSpectatorMenu", ModernManhunt.getInstance());
        if (p.hasMetadata("OpenedTPMenu"))
            p.removeMetadata("OpenedTPMenu", ModernManhunt.getInstance());
        if (p.getMetadata("OpenedRecipesMenu").isEmpty()) return;

        if (p.getMetadata("OpenedRecipesMenu").getFirst().asString().equals("Recipes Menu")) {
            p.removeMetadata("OpenedRecipesMenu", ModernManhunt.getInstance());
        } else {
            Bukkit.getScheduler().runTask(ModernManhunt.getInstance(), () -> {
                MMFunctions.OpenRecipesMenu(p);
            });
        }
    }
}
