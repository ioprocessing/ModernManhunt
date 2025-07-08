package me.Ford.modernManhunt;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BlockListener implements Listener {



    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (handItem.getItemMeta() != null && handItem.getItemMeta().getPersistentDataContainer().has(Keys.PRIMED_PICK) && Collections.SMELT.containsKey(b.getType())) {

            // Set the drop to the corresponding smelt map item then cancel the original drop
            ItemStack drop = new ItemStack(Collections.SMELT.get(b.getType()));
            int stack_size = 1;

            // Set increased stack size for copper ore
            if (drop.getType().equals(Material.COPPER_INGOT)) {
                stack_size = MMFunctions.random(5, 2);
            }

            if (Math.random() - (2f / (handItem.getEnchantmentLevel(Enchantment.FORTUNE) + 2)) > 0) {
                stack_size *= MMFunctions.random(handItem.getEnchantmentLevel(Enchantment.FORTUNE)+1, 2);
            }

            drop.setAmount(stack_size);
            event.setDropItems(false);
            b.getWorld().dropItemNaturally(b.getLocation(), drop);

            // Increment EXP values on the pickaxe
            ItemMeta meta = handItem.getItemMeta();
            float currentExp = meta.getPersistentDataContainer().get(Keys.PRIMED_PICK_EXP, PersistentDataType.FLOAT);
            currentExp += Collections.EXP.get(drop.getType());
            meta.getPersistentDataContainer().set(Keys.PRIMED_PICK_EXP, PersistentDataType.FLOAT, currentExp);
            if(currentExp >= 1.0f) {
                ExperienceOrb orb = b.getWorld().spawn(b.getLocation(), ExperienceOrb.class);
                orb.setExperience(1);
                meta.getPersistentDataContainer().set(Keys.PRIMED_PICK_EXP, PersistentDataType.FLOAT, currentExp - 1.0f);
            }
            handItem.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemMeta handItem = event.getItemInHand().getItemMeta();
        Player player = event.getPlayer();

        // Check if the item being clicked with has the 'CONSUMABLE_HEAD' key
        if (handItem.getPersistentDataContainer().has(Keys.CONSUMABLE_HEAD)) {

            // Then cancel the event and 'consume' the item
            event.setCancelled(true);
            PlayerHeads.consumePlayerHead(player, event.getItemInHand());

        }

        // Separate effect for gheads
        else if (handItem.getPersistentDataContainer().has(Keys.GOLDEN_HEAD)) {

            // Then cancel the event and 'consume' the item
            event.setCancelled(true);
            PlayerHeads.consumeGoldenHead(player, event.getItemInHand());

        }

    }
}
