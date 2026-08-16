package org.ioprocessing.modernManhunt.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.ioprocessing.modernManhunt.Core;

import java.util.HashMap;
import java.util.Map;

public class CustomItems implements Listener {
    // Item tags
    public static final NamespacedKey CUSTOM_ITEM = new NamespacedKey(Core.getInstance(), "CustomItem");
    public static final NamespacedKey PREVENT_ITEM_DROP = new NamespacedKey(Core.getInstance(), "DeleteItemOnDeath");

    public static final Map<String, ItemStack> CUSTOM_ITEM_MAP = new HashMap<>(Map.ofEntries(
            Map.entry("primed_pickaxe", PrimedPickaxe.getPrimedPickaxe()),
            Map.entry("player_head", ConsumableHeads.getConsumablePlayerHead(null)),
            Map.entry("golden_head", ConsumableHeads.getGoldenHead()),
            Map.entry("hunter_compass", HunterCompass.getHunterCompass()),
            Map.entry("spectator_compass", SpectatorCompass.getSpectatorCompass()),
            Map.entry("dummy_map", TrialChamberMap.getDummyMap()),
            Map.entry("tp_star", TPStar.getTPStar()),
            Map.entry("bolstered_bow", CustomWeapons.getBolsteredBow()),
            Map.entry("strengthened_sword", CustomWeapons.getStrengthenedSword()),
            Map.entry("fortress_compass", FortressCompass.getFortressCompass())
    ));

    /**
     * Helper methods to easily identify item keys.
     */
    public static boolean isCustomItem(ItemStack item) {
        return hasKey(item, CUSTOM_ITEM);
    }

    public static boolean hasKey(ItemStack item, NamespacedKey key) {
        if(item == null || item.getItemMeta() == null)
            return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
    }

    public static boolean isPreventItemDrop(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;
        return item.getItemMeta().getPersistentDataContainer().has(PREVENT_ITEM_DROP, PersistentDataType.BOOLEAN);
    }

    /**
     * Prevent crafting other items using custom items.
     */
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        for (int i = 1; i < 10; i++) {
            // Check each item in the crafting menu
            ItemStack item = e.getInventory().getItem(i);
            if (item != null) {
                // If the item is a custom item and isn't a consumable head, cancel the crafting recipe
                if (isCustomItem(item) && !ConsumableHeads.isPlayerHead(item))
                    e.getInventory().setResult(null);
            }
        }
    }

    /**
     * Prevent anvil usage if either slots has a custom item.
     */
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getFirstItem();
        ItemStack second = event.getInventory().getSecondItem();
        if ((isCustomItem(first)) || isCustomItem(second)) {
            event.setResult(null);
        }
    }

    /**
     * Prevent grindstone usage if either slots has a custom item.
     */
    @EventHandler
    public void onPrepareGrind(PrepareGrindstoneEvent event) {
        ItemStack upper = event.getInventory().getUpperItem();
        ItemStack lower = event.getInventory().getLowerItem();
        if ((isCustomItem(upper) || isCustomItem(lower))) {
            event.setResult(null);
        }
    }

    /**
     * Prevent enchanting a custom item.
     */
    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        if (isCustomItem(event.getItem())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent players from dropping no-drop items.
     */
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if(!isActiveManhunt()) return;

        ItemStack item = event.getItemDrop().getItemStack();
        if (isPreventItemDrop(item))
            event.setCancelled(true);
    }

    /**
     * Prevent dropping no-drop items on death.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!isActiveManhunt()) return;

        Player p = e.getPlayer();
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null) {
                if (isPreventItemDrop(item))
                    e.getDrops().remove(item);
            }
        }
    }

    /**
     * Prevent moving no-drop items into external containers.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!isActiveManhunt()) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory topInventory = event.getView().getTopInventory();
        boolean topIsPlayerInv = topInventory.getType() == InventoryType.CRAFTING
                || topInventory.getType() == InventoryType.PLAYER;

        if (topIsPlayerInv) return;

        ItemStack moved;

        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY -> moved = event.getCurrentItem();
            case PLACE_ONE, PLACE_SOME, PLACE_ALL, SWAP_WITH_CURSOR -> moved = event.getCursor();
            case HOTBAR_SWAP -> {
                int hotbarSlot = event.getHotbarButton();
                moved = hotbarSlot >= 0
                        ? player.getInventory().getItem(hotbarSlot)
                        : player.getInventory().getItemInOffHand();
            }
            default -> { return; }
        }

        boolean targetIsExternal = event.getClickedInventory() != null
                && event.getClickedInventory().equals(topInventory);

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || targetIsExternal) {
            if (isPreventItemDrop(moved)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevent moving no-drop items into external containers (via dragging).
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if(!isActiveManhunt()) return;

        if (!(event.getWhoClicked() instanceof Player)) return;

        if (!isPreventItemDrop(event.getOldCursor())) return;

        Inventory topInventory = event.getView().getTopInventory();
        boolean topIsPlayerInv = topInventory.getType() == InventoryType.CRAFTING
                || topInventory.getType() == InventoryType.PLAYER;

        if (topIsPlayerInv) return;

        // Check if any of the drag target slots are in the external container
        int topSize = topInventory.getSize();
        for (int slot : event.getRawSlots()) {
            if (slot < topSize) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Prevent inserting no-drop items into a bundle via clicking.
     */
    @EventHandler
    public void onInventoryClickBundleInsert(InventoryClickEvent event) {
        if(!isActiveManhunt()) return;

        if (!(event.getWhoClicked() instanceof Player)) return;

        Inventory topInventory = event.getView().getTopInventory();
        boolean topIsPlayerInv = topInventory.getType() == InventoryType.CRAFTING
                || topInventory.getType() == InventoryType.PLAYER;
        if (!topIsPlayerInv) return;

        if (!event.getClick().isLeftClick()) return;

        ItemStack cursor = event.getCursor();
        ItemStack clicked = event.getCurrentItem();

        if (cursor.getType().isAir() || clicked == null) return;

        // Cursor is a bundle & clicked slot has a protected item
        if (cursor.getType() == Material.BUNDLE && isPreventItemDrop(clicked)) {
            event.setCancelled(true);
            return;
        }

        // Cursor is a protected item & clicked slot is a bundle
        if (clicked.getType() == Material.BUNDLE && isPreventItemDrop(cursor)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent picking up items when a no-drop item is on the player's cursor.
     */
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if(!isActiveManhunt()) return;

        if (!(event.getEntity() instanceof Player player)) return;
        if (isPreventItemDrop(player.getItemOnCursor())) {
            event.setCancelled(true);
        }
    }
    
    private boolean isActiveManhunt() {
        return Core.getInstance().isActiveManhunt();
    }
}
