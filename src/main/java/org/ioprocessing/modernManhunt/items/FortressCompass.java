package org.ioprocessing.modernManhunt.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StructureSearchResult;
import org.ioprocessing.modernManhunt.Core;

import java.util.Arrays;

public class FortressCompass implements Listener {

    public static final NamespacedKey FORTRESS_COMPASS_KEY = new NamespacedKey(Core.getInstance(), "FortressCompass");

    /**
     * @return Fortress Compass item
     */
    public static ItemStack getFortressCompass() {
        return getFortressCompass(null);
    }

    /**
     * @return Fortress Compass item (with Fortress pre-selected)
     */
    public static ItemStack getFortressCompass(Player player) {
        ItemStack fortressCompass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) fortressCompass.getItemMeta();

        TextComponent compassName = Component.text("Nether Fortress Compass", NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false);

        meta.lore(compassLore());

        meta.getPersistentDataContainer().set(FORTRESS_COMPASS_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.PREVENT_ITEM_DROP, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        fortressCompass.setItemMeta(meta);
        fortressCompass.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker().build());
        fortressCompass.setData(DataComponentTypes.CUSTOM_NAME, compassName); // Unfortunately lodestone tracker overrides item name to "lodestone compass"
        fortressCompass.setData(DataComponentTypes.RARITY, ItemRarity.RARE);

        if(player != null)
            locateNearestFortress(player, fortressCompass);

        return fortressCompass;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCompassUse(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        ItemStack compass = event.getItem();
        if (!CustomItems.hasKey(compass, FORTRESS_COMPASS_KEY)) return;

        Player player = event.getPlayer();

        if (!isInNether(player)) {
            player.sendActionBar(Component.text("You must be in the Nether to locate a Nether Fortress", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
            return;
        }

        locateNearestFortress(player, compass);
    }

    /**
     * Locates the nearest nether fortress and points the compass at it.
     */
    private static void locateNearestFortress(Player player, ItemStack compass) {
        Location searchOrigin = player.getLocation().clone();

        StructureSearchResult fortress = player.getWorld().locateNearestStructure(
                searchOrigin,
                Structure.FORTRESS,
                10_000,
                false
        );

        if(fortress == null) {
            player.sendActionBar(Component.text("No Nether Fortress was found nearby", NamedTextColor.RED));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
            return;
        }

        Location fortressLoc = fortress.getLocation();

        if (!player.isOnline()) return;

        pointCompassAt(compass, fortressLoc);

        player.sendActionBar(fortressActionBarText(fortressLoc, isExactCoordsHandicap(player)));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 0.5f);
    }

    /**
     * Sets the compass lodestone tracker to point at the given location.
     */
    private static void pointCompassAt(ItemStack compass, Location target) {
        LodestoneTracker tracker = LodestoneTracker.lodestoneTracker(target, false);
        compass.setData(DataComponentTypes.LODESTONE_TRACKER, tracker);
    }

    private boolean isInNether(Player player) {
        return player.getWorld().getEnvironment() == org.bukkit.World.Environment.NETHER;
    }

    /** Send formatted text for the action bar, including coordinates if the player has that handicap */
    private static Component fortressActionBarText(Location fortress, boolean exactCoords) {
        TextComponent.Builder builder = Component.text()
                .append(Component.text("Nether Fortress located"));
        if(exactCoords) {
            int X = fortress.getBlockX();
            int Z = fortress.getBlockZ();
            builder.append(Component.text(String.format(" at X: %s Z: %s", X, Z), NamedTextColor.WHITE));
        }
        return builder.build();
    }

    private static java.util.List<TextComponent> compassLore() {
        return Arrays.asList(
                Component.text("A compass that points toward Nether Fortresses.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text()
                        .append(Component.text("USE").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                        .append(Component.text(" to find the nearest Fortress").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build()
        );
    }

    private static boolean isExactCoordsHandicap(Player player) {
        return Core.getInstance().getHandicap().getConfig().isExactCoordsHandicap(player.getUniqueId());
    }
}