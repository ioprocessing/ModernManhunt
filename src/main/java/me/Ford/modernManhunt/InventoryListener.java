package me.Ford.modernManhunt;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.persistence.PersistentDataType;

public class InventoryListener implements Listener {
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getInventory().getResult() != null && event.getInventory().getResult().getItemMeta().getPersistentDataContainer().has(Keys.GOLDEN_HEAD, PersistentDataType.BOOLEAN)) {
            if (!event.getInventory().getItem(5).getItemMeta().getPersistentDataContainer().has(Keys.CONSUMABLE_HEAD, PersistentDataType.BOOLEAN)) {
                event.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if ((event.getInventory().getFirstItem() != null && event.getInventory().getFirstItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN))
                || (event.getInventory().getSecondItem() != null && event.getInventory().getSecondItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN))) {
            event.setResult(null);
        }
    }

    @EventHandler
    public void onPrepareGrind(PrepareGrindstoneEvent event) {
        if ((event.getInventory().getUpperItem() != null && event.getInventory().getUpperItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN))
                || (event.getInventory().getLowerItem() != null && event.getInventory().getLowerItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN))) {
            event.setResult(null);
        }
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        if (event.getItem().getItemMeta().getPersistentDataContainer().has(Keys.CUSTOM_ITEM, PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);
        }
    }
}
