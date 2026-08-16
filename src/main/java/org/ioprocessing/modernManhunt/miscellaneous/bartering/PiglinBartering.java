package org.ioprocessing.modernManhunt.miscellaneous.bartering;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.BarterPoolConfig;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class PiglinBartering implements Listener {
    private final ConfigManager<BarterPoolConfig> bartering;
    private final Random random = new Random();

    public PiglinBartering(Core plugin) {
        this.bartering = plugin.getBartering();
    }

    /**
     * When a piglin barters, replace the loot with the custom loot (if enabled)
     */
    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent event) {
        if(!bartering.getConfig().isEnabled())
            return;

        List<ItemStack> originalLoot = event.getOutcome();

        originalLoot.clear();
        originalLoot.add(generateBarterItem());
    }

    /**
     * Generate the barter item to be dropped
     */
    private ItemStack generateBarterItem() {
        List<BarterPoolConfig.LootEntry> barteringPool = bartering.getConfig().getBarteringPool();

        // First generate the loot entry that we'll drop
        BarterPoolConfig.LootEntry entry = generateBarterLootEntry(barteringPool);

        // Then, construct the actual ItemStack to replace the original loot with
        return constructBarterItem(entry);
    }

    /**
     * Construct the item to drop based on the loot table entry
     */
    private ItemStack constructBarterItem(BarterPoolConfig.LootEntry entry) {
        if(entry == null) {
            return null;
        }

        // Create base item
        ItemStack item = new ItemStack(entry.getMaterial());

        // Set the ItemStack quantity
        int quantity = random(entry.getMinQuantity(), entry.getMaxQuantity());
        item.setAmount(quantity);

        // If the item is enchanted, add the enchantment
        if(entry.getEnchantment() != null) {
            int enchantmentLevel = random(entry.getMinEnchantmentLevel(), entry.getMaxEnchantmentLevel());
            item.addUnsafeEnchantment(
                    getEnchantment(entry.getEnchantment()),
                    enchantmentLevel);
        }

        // If the item is a potion, add the potion effect
        if(entry.getPotionType() != null && (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION)) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
            potionMeta.setBasePotionType(entry.getPotionType());
            item.setItemMeta(potionMeta);
        }

        return item;
    }

    /**
     * Generate the loot entry to pick based on weight
     */
    private BarterPoolConfig.LootEntry generateBarterLootEntry(List<BarterPoolConfig.LootEntry> barteringPool) {
        // Calculate total weight
        int totalWeight = 0;
        for (BarterPoolConfig.LootEntry entry : barteringPool) {
            totalWeight += entry.getWeight();
        }

        if (totalWeight <= 0) {
            return null;
        }

        int roll = random.nextInt(totalWeight);

        // Find the selected entry
        int currentWeight = 0;
        for (BarterPoolConfig.LootEntry entry : barteringPool) {
            currentWeight += entry.getWeight();
            if (roll < currentWeight) {
                return entry;
            }
        }

        // Fallback
        return barteringPool.getFirst();
    }

    /**
     * Roll a random number between min and max (inclusive)
     */
    private int random(int min, int max) {
        if (min >= max) {
            return min;
        }
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Get enchantment from a string
     */
    public static Enchantment getEnchantment(String id) {
        if (id == null) return null;

        NamespacedKey key = id.contains(":")
                ? NamespacedKey.fromString(id)
                : NamespacedKey.minecraft(id.toLowerCase());

        if (key == null) {
            Core.logger().error("Could not find enchantment {}", id);
            return null;
        }

        Registry<@NotNull Enchantment> registry =
                RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

        return registry.get(key);
    }
}
