package me.Ford.modernManhunt.CustomItemListeners;

import me.Ford.modernManhunt.Collections;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.Keys;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class PrimedPickaxeListener implements Listener {



    @EventHandler
    public void onPrimedPickaxeBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (handItem.getItemMeta() != null && handItem.getItemMeta().getPersistentDataContainer().has(Keys.PRIMED_PICK) && Collections.SMELT.containsKey(b.getType())) {

            // Set the drop to the corresponding smelt map item then cancel the original drop
            ItemStack drop = new ItemStack(Collections.SMELT.get(b.getType()));
            int stack_size = 1;

            // Set increased stack size for copper ore
            if (drop.getType().equals(Material.COPPER_INGOT)) {
                stack_size = Functions.random(5, 2);
            }

            // Recreate Minecraft's fortune logic
            if (Math.random() - (2f / (handItem.getEnchantmentLevel(Enchantment.FORTUNE) + 2)) > 0) {
                stack_size *= Functions.random(handItem.getEnchantmentLevel(Enchantment.FORTUNE)+1, 2);
            }

            // Replace the dropped items and show the flame particle effects
            drop.setAmount(stack_size);
            event.setDropItems(false);
            b.getWorld().dropItemNaturally(b.getLocation(), drop);
            b.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);

            // Increment EXP values on the pickaxe
            ItemMeta meta = handItem.getItemMeta();
            float currentExp = meta.getPersistentDataContainer().get(Keys.PRIMED_PICK_EXP, PersistentDataType.FLOAT);
            currentExp += Collections.EXP.get(drop.getType());
            meta.getPersistentDataContainer().set(Keys.PRIMED_PICK_EXP, PersistentDataType.FLOAT, currentExp);

            // If the total stored exp > 1, deposit the XP in the world like a furnace would
            if(currentExp >= 1.0f) {
                ExperienceOrb orb = b.getWorld().spawn(b.getLocation(), ExperienceOrb.class);
                orb.setExperience(1);
                meta.getPersistentDataContainer().set(Keys.PRIMED_PICK_EXP, PersistentDataType.FLOAT, currentExp - 1.0f);
            }
            handItem.setItemMeta(meta);
        }
    }
}
