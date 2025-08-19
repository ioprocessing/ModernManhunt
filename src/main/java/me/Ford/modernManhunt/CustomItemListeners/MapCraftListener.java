package me.Ford.modernManhunt.CustomItemListeners;

import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.persistence.PersistentDataType;

public class MapCraftListener implements Listener {
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e){
        if (e.getInventory().getResult() != null && e.getInventory().getResult().getItemMeta().getPersistentDataContainer().has(Keys.DUMMY_MAP, PersistentDataType.BOOLEAN))
            e.getInventory().setResult(CustomItems.trialChamberMap((Player) e.getView().getPlayer()));
    }
}
