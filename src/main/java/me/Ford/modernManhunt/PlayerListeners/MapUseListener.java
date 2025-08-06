package me.Ford.modernManhunt.PlayerListeners;

import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Keys;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MapUseListener implements Listener {
    @EventHandler
    public void onMapClick(PlayerInteractEvent e) {
        if (e.getAction().isLeftClick())
            return;
        if (e.getItem() != null) {
            ItemStack heldItem = e.getItem();
            if (heldItem.getPersistentDataContainer().has(Keys.DUMMY_MAP)) {
                EquipmentSlot hand = e.getHand();
                heldItem = CustomItems.trialChamberMap(e.getPlayer());
                e.getPlayer().getInventory().setItem(hand, heldItem);
            }
        }
    }
}
