package me.Ford.modernManhunt.OtherListeners;

import me.Ford.modernManhunt.Collections;
import me.Ford.modernManhunt.Functions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PiglinBarterListener implements Listener {
    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent event) {
        // Register original barter loot
        List<ItemStack> originalLoot = event.getOutcome();

        // Clear and replace with custom drops
        originalLoot.clear();
        originalLoot.add(Functions.getCustomLoot(Collections.CUSTOM_BARTER_POOL));
    }
}
