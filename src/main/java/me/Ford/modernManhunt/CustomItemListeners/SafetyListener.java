package me.Ford.modernManhunt.CustomItemListeners;

import me.Ford.modernManhunt.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SafetyListener implements Listener {
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getInventory().getResult() != null && event.getInventory().getResult().getItemMeta().getPersistentDataContainer().has(Keys.GOLDEN_HEAD, PersistentDataType.BOOLEAN)) {
            if (!event.getInventory().getItem(5).getItemMeta().getPersistentDataContainer().has(Keys.CONSUMABLE_HEAD, PersistentDataType.BOOLEAN)) {
                event.getInventory().setResult(null);
            }
        } else for (int i = 1; i < 10; i++) {
            // Check each item in the crafting menu
            ItemStack item = event.getInventory().getItem(i);
            if (item != null) {
                PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                // If the item is a custom item and isn't a consumable head, cancel the crafting recipe
                if (data.has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN) && !data.has(Keys.CONSUMABLE_HEAD, PersistentDataType.BOOLEAN))
                    event.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        // Prevent using the anvil on custom items
        if ((event.getInventory().getFirstItem() != null && event.getInventory().getFirstItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN))
                || (event.getInventory().getSecondItem() != null && event.getInventory().getSecondItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN))) {
            event.setResult(null);
        }
    }

    @EventHandler
    public void onPrepareGrind(PrepareGrindstoneEvent event) {
        // Prevent using the grindstone on custom items
        if ((event.getInventory().getUpperItem() != null && event.getInventory().getUpperItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN))
                || (event.getInventory().getLowerItem() != null && event.getInventory().getLowerItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN))) {
            event.setResult(null);
        }
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        // Prevent using the enchanting table on custom items
        if (event.getItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        PersistentDataContainer item = event.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer();
        // Check what item is being dropped; if it's the tracking/teleportation items, prevent it
        if (item.has(Keys.HUNTER_COMPASS) || item.has(Keys.SPECTATOR_COMPASS) || item.has(Keys.TP_STAR))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null) {
                // Check what item is being dropped; if it's the tracking/teleportation items, prevent it
                PersistentDataContainer d = item.getItemMeta().getPersistentDataContainer();
                if (d.has(Keys.HUNTER_COMPASS) || d.has(Keys.SPECTATOR_COMPASS) || d.has(Keys.TP_STAR))
                    e.getDrops().remove(item);
            }
        }
    }
}
