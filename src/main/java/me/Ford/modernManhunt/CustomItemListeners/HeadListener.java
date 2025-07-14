package me.Ford.modernManhunt.CustomItemListeners;

import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class HeadListener implements Listener {

    @EventHandler
    public void onHeadClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        // If the player right clicks with an item,
        if (e.getItem() != null && e.getAction().isRightClick() && e.getClickedBlock() == null) {

            // Check if the item being clicked with has the 'CONSUMABLE_HEAD' key
            PersistentDataContainer d = e.getItem().getItemMeta().getPersistentDataContainer();
            if (d.has(Keys.CONSUMABLE_HEAD)) {

                // Then cancel the event and 'consume' the item
                e.setCancelled(true);
                Functions.consumePlayerHead(p, e.getItem());
            }

            // Separate effect for golden heads
            else if (d.has(Keys.GOLDEN_HEAD)) {

                // Then cancel the event and 'consume' the item
                e.setCancelled(true);
                Functions.consumeGoldenHead(p, e.getItem());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        PersistentDataContainer d = e.getItemInHand().getItemMeta().getPersistentDataContainer();
        Player p = e.getPlayer();

        // Check if the item being clicked with has the 'CONSUMABLE_HEAD' key
        if (d.has(Keys.CONSUMABLE_HEAD)) {

            // Then cancel the event and 'consume' the item
            e.setCancelled(true);
            Functions.consumePlayerHead(p, e.getItemInHand());

        }

        // Separate effect for golden heads
        else if (d.has(Keys.GOLDEN_HEAD)) {

            // Then cancel the event and 'consume' the item
            e.setCancelled(true);
            Functions.consumeGoldenHead(p, e.getItemInHand());
        }
    }
}
