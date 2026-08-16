package org.ioprocessing.modernManhunt.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;

import java.util.Arrays;
import java.util.List;

public class ConsumableHeads implements Listener {
    private final ConfigManager<CoreConfig> coreConfig;

    public static final NamespacedKey CONSUMABLE_PLAYER_HEAD_KEY = new NamespacedKey(Core.getInstance(), "ConsumablePlayerHead");
    public static final NamespacedKey GOLDEN_HEAD_KEY = new NamespacedKey(Core.getInstance(), "GoldenHead");

    public static final NamespacedKey CONSUMABLE_PLAYER_HEAD_COOLDOWN = new NamespacedKey(Core.getInstance(), "ConsumablePlayerHeadCooldown");
    public static final NamespacedKey GOLDEN_HEAD_COOLDOWN = new NamespacedKey(Core.getInstance(), "ConsumableHeadCooldown");

    public ConsumableHeads(Core plugin) {
        this.coreConfig = plugin.getCoreConfig();
    }

    /**
     * @param player The player skin which the head will match (null = Steve)
     * @return Consumable player head
     */
    public static ItemStack getConsumablePlayerHead(Player player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();


        // Custom item properties
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Speed I (15s)").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("and Regeneration II (5s). Also").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text()
                        .append(Component.text("used to craft ").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text("Golden Heads").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build()));

        meta.getPersistentDataContainer().set(CONSUMABLE_PLAYER_HEAD_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        UseCooldownComponent cd = meta.getUseCooldown();
        cd.setCooldownGroup(CONSUMABLE_PLAYER_HEAD_COOLDOWN);
        meta.setUseCooldown(cd);
        meta.setOwningPlayer(player);

        playerHead.setItemMeta(meta);

        if(player != null) {
            TextComponent headName = Component.text(player.getName() + "'s Head").decoration(TextDecoration.ITALIC, false);
            playerHead.setData(DataComponentTypes.ITEM_NAME, headName);
        }
        playerHead.setData(DataComponentTypes.RARITY, ItemRarity.RARE);

        return playerHead;
    }

    /**
     * @return Consumable golden head
     */
    public static ItemStack getGoldenHead() {
        ItemStack goldenHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) goldenHead.getItemMeta();

        // Custom item properties
        TextComponent headName = Component.text("Golden Head").decoration(TextDecoration.ITALIC, false);
        meta.lore(Arrays.asList(
                Component.text()
                        .append(Component.text("CONSUME").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to gain Regeneration III (5s),").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build(),
                Component.text("Speed II (15s), and Absorption I (120s)").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("golden_head"));
        meta.setCustomModelDataComponent(model);

        meta.getPersistentDataContainer().set(GOLDEN_HEAD_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        UseCooldownComponent cd = meta.getUseCooldown();
        cd.setCooldownGroup(GOLDEN_HEAD_COOLDOWN);
        meta.setUseCooldown(cd);

        goldenHead.setItemMeta(meta);
        goldenHead.setData(DataComponentTypes.ITEM_NAME, headName);
        goldenHead.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);

        return goldenHead;
    }

    /**
     * When a player right clicks while holding a consumable head,
     * apply effects and remove the head from their hand
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onHeadClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        // If the player right clicks with an item,
        if (e.getItem() != null && e.getAction().isRightClick() && e.getClickedBlock() == null) {

            // Check if the item being clicked with has the 'CONSUMABLE_HEAD' key
            PersistentDataContainer d = e.getItem().getItemMeta().getPersistentDataContainer();
            if (d.has(CONSUMABLE_PLAYER_HEAD_KEY)) {

                // Then cancel the event and 'consume' the item
                e.setCancelled(true);
                consumePlayerHead(p, e.getItem());
            }

            // Separate effect for golden heads
            else if (d.has(GOLDEN_HEAD_KEY)) {

                // Then cancel the event and 'consume' the item
                e.setCancelled(true);
                consumeGoldenHead(p, e.getItem());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHeadPlace(BlockPlaceEvent e) {
        PersistentDataContainer d = e.getItemInHand().getItemMeta().getPersistentDataContainer();
        Player p = e.getPlayer();

        // Check if the item being clicked with has the 'CONSUMABLE_HEAD' key
        if (d.has(CONSUMABLE_PLAYER_HEAD_KEY)) {

            // Then cancel the event and 'consume' the item
            e.setCancelled(true);
            consumePlayerHead(p, e.getItemInHand());

        }

        // Separate effect for golden heads
        else if (d.has(GOLDEN_HEAD_KEY)) {

            // Then cancel the event and 'consume' the item
            e.setCancelled(true);
            consumeGoldenHead(p, e.getItemInHand());
        }
    }

    /**
     * Internal function for applying the head effects to the player
     * @param player The player that receives the potion effects and cooldown
     * @param head The item that is getting removed from the player's inventory
     */
    private void consumePlayerHead(Player player, ItemStack head) {
        // If item is on cooldown, do nothing
        if (player.getCooldown(CONSUMABLE_PLAYER_HEAD_COOLDOWN) > 0) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

        // Potion effects and cooldown
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 0));
        if(coreConfig.getConfig().getCooldowns().isEnabled())
            player.setCooldown(CONSUMABLE_PLAYER_HEAD_COOLDOWN,
                    coreConfig
                            .getConfig()
                            .getCooldowns()
                            .getPlayerHeadCooldownSeconds() * 20);

        // Lower stack count after consumption
        head.setAmount(head.getAmount() - 1);
    }

    private void consumeGoldenHead(Player player, ItemStack head) {
        // If item is on cooldown, do nothing
        if (player.getCooldown(GOLDEN_HEAD_COOLDOWN) > 0) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);

        // Potion effects and cooldown
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
        if(coreConfig.getConfig().getCooldowns().isEnabled())
            player.setCooldown(GOLDEN_HEAD_COOLDOWN,
                    coreConfig
                            .getConfig()
                            .getCooldowns()
                            .getGoldenHeadCooldownSeconds() * 20);

        // Lower stack count after consumption
        head.setAmount(head.getAmount() - 1);
    }

    // Helper methods for checking consumable head types/validity
    public static boolean isGoldenHead(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(GOLDEN_HEAD_KEY, PersistentDataType.BOOLEAN);
    }

    public static boolean isPlayerHead(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().has(CONSUMABLE_PLAYER_HEAD_KEY, PersistentDataType.BOOLEAN);
    }

    /**
     * Prevent crafting golden heads with other golden heads and/or non-MM heads
     */
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        ItemStack result = e.getInventory().getResult();
        if (result != null && isGoldenHead(result)) {
            ItemStack centerItem = e.getInventory().getItem(5);
            if (centerItem != null && !isPlayerHead(centerItem)) {
                e.getInventory().setResult(null);
            }
        }
    }
}
