package org.ioprocessing.modernManhunt.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.ioprocessing.modernManhunt.Core;

import java.util.*;

public class PrimedPickaxe implements Listener {

    private final Random random = new Random();

    public static final NamespacedKey PRIMED_PICKAXE_KEY = new NamespacedKey(Core.getInstance(), "PrimedPickaxe");
    public static final NamespacedKey PRIMED_PICKAXE_EXP = new NamespacedKey(Core.getInstance(), "PrimedPickaxeEXP");

    // Broken blocks -> associated smelt drop
    private final Map<Material, Material> SMELT_ITEMS = new HashMap<>(Map.ofEntries(
            Map.entry(Material.IRON_ORE, Material.IRON_INGOT),
            Map.entry(Material.COPPER_ORE, Material.COPPER_INGOT),
            Map.entry(Material.GOLD_ORE, Material.GOLD_INGOT),
            Map.entry(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT),
            Map.entry(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT),
            Map.entry(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT),
            Map.entry(Material.RAW_IRON_BLOCK, Material.IRON_BLOCK),
            Map.entry(Material.RAW_COPPER_BLOCK, Material.COPPER_BLOCK),
            Map.entry(Material.RAW_GOLD_BLOCK, Material.GOLD_BLOCK),
            Map.entry(Material.SAND, Material.GLASS),
            Map.entry(Material.RED_SAND, Material.GLASS),
            Map.entry(Material.WET_SPONGE, Material.SPONGE)
    ));

    // How much EXP each smelt is worth (based off vanilla)
    public static final Map<Material, Float> SMELT_EXP = new HashMap<>(Map.ofEntries(
            Map.entry(Material.IRON_INGOT, 0.7f),
            Map.entry(Material.COPPER_INGOT, 0.7f),
            Map.entry(Material.GOLD_INGOT, 1.0f),
            Map.entry(Material.IRON_BLOCK, 0.7f),
            Map.entry(Material.COPPER_BLOCK, 0.7f),
            Map.entry(Material.GOLD_BLOCK, 1.0f),
            Map.entry(Material.GLASS, 0.1f),
            Map.entry(Material.SPONGE, 0.15f)
    ));


    /**
     * @return Primed Pickaxe custom item
     */
    public static ItemStack getPrimedPickaxe() {
        ItemStack primePick = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta meta = primePick.getItemMeta();

        // Custom item properties
        TextComponent pickaxeName = Component.text("Primed Pickaxe", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.addEnchant(Enchantment.EFFICIENCY, 2, false);
        meta.addEnchant(Enchantment.UNBREAKING, 1, false);
        meta.lore(Arrays.asList(
                Component.text("An imbued pickaxe that automatically").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("smelts certain types of blocks").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("primed_pickaxe"));
        meta.setCustomModelDataComponent(model);

        meta.getPersistentDataContainer().set(PRIMED_PICKAXE_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        // Track accumulated experience points
        meta.getPersistentDataContainer().set(PRIMED_PICKAXE_EXP, PersistentDataType.FLOAT, 0f);

        primePick.setItemMeta(meta);
        primePick.setData(DataComponentTypes.ITEM_NAME, pickaxeName);

        return primePick;
    }

    /**
     * When a block is broken and the player is holding the Primed Pickaxe,
     * increment internal pickaxe EXP and modify drops as necessary
     */
    @EventHandler
    public void onPrimedPickaxeBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (handItem.getItemMeta() != null && handItem.getItemMeta().getPersistentDataContainer().has(PRIMED_PICKAXE_KEY) && SMELT_ITEMS.containsKey(block.getType())) {

            // Set the drop to the corresponding smelt map item then cancel the original drop
            ItemStack drop = new ItemStack(SMELT_ITEMS.get(block.getType()));
            int stack_size = 1;

            // Set increased stack size for copper ore
            if (drop.getType().equals(Material.COPPER_INGOT)) {
                stack_size = random(2, 5);
            }

            // Recreate Minecraft's fortune logic
            if (Math.random() - (2f / (handItem.getEnchantmentLevel(Enchantment.FORTUNE) + 2)) > 0) {
                stack_size *= random(2, handItem.getEnchantmentLevel(Enchantment.FORTUNE)+1);
            }

            // Replace the dropped items and show the flame particle effects
            drop.setAmount(stack_size);
            event.setDropItems(false);

            // Create location in the middle of the block instead of the edge coordinates
            Location loc = block.getLocation();
            Location midLoc = new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY(), loc.getZ() + 0.5);

            block.getWorld().dropItemNaturally(midLoc, drop);
            block.getWorld().playEffect(midLoc, Effect.MOBSPAWNER_FLAMES, 1);

            // Increment EXP values on the pickaxe
            ItemMeta meta = handItem.getItemMeta();
            Float currentExp = meta.getPersistentDataContainer().get(PRIMED_PICKAXE_EXP, PersistentDataType.FLOAT);
            currentExp += SMELT_EXP.get(drop.getType()); // Add the specific EXP value for that item drop
            meta.getPersistentDataContainer().set(PRIMED_PICKAXE_EXP, PersistentDataType.FLOAT, currentExp);

            // If the total stored exp > 1, deposit the XP in the world like a furnace would
            if(currentExp >= 1.0f) {
                ExperienceOrb orb = block.getWorld().spawn(midLoc, ExperienceOrb.class);
                orb.setExperience(1);
                meta.getPersistentDataContainer().set(PRIMED_PICKAXE_EXP, PersistentDataType.FLOAT, currentExp - 1.0f);
            }
            handItem.setItemMeta(meta);
        }
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
}
