package org.ioprocessing.modernManhunt.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StructureSearchResult;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;

import java.util.Arrays;

public class TrialChamberMap implements Listener {
    public static final NamespacedKey DUMMY_MAP_KEY = new NamespacedKey(Core.getInstance(), "DummyMap");

    /**
     * @return Dummy item that turns into a Trial Chambers explorer map when clicked
     */
    public static ItemStack getDummyMap() {
        ItemStack trialMap = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) trialMap.getItemMeta();

        // Custom map properties (set map color)
        mapMeta.setColor(Color.fromRGB(194, 107, 76));
        TextComponent map_name = Component.text("Trial Explorer Map", TextColor.color(206, 112, 43)).decoration(TextDecoration.ITALIC, false);
        mapMeta.customName(map_name);

        String miniMsg = "<!i><white>USE <gray>to reveal the way to";
        Component message = MiniMessage.miniMessage().deserialize(miniMsg);

        mapMeta.lore(Arrays.asList(message,
                Component.text("the nearest Trial Chambers").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        mapMeta.getPersistentDataContainer().set(DUMMY_MAP_KEY, PersistentDataType.BOOLEAN, true);
        mapMeta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        trialMap.setItemMeta(mapMeta);
        return trialMap;
    }

    /**
     * Transform dummy maps into Trial Chambers explorer maps when player right clicks with it
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack clickedItem = event.getItem();
        Player player = event.getPlayer();

        if(event.getAction().isRightClick() && event.getHand() != null && CustomItems.hasKey(clickedItem, DUMMY_MAP_KEY)) {
            player.getEquipment().setItem(event.getHand(), trialChamberMap(player));
        }
    }

    /**
     * We have to use NMS to create a trial chamber explorer map since Bukkit
     * doesn't expose Trial Chambers as a valid structure for their method
     */
    public static ItemStack trialChamberMap(Player player) {
        World bukkitWorld = player.getWorld();
        ServerLevel nmsWorld = ((CraftWorld) bukkitWorld).getHandle();

        // Locate nearest Trial Chambers structure
        StructureSearchResult result = bukkitWorld.locateNearestStructure(
                player.getLocation(),
                Structure.TRIAL_CHAMBERS,
                10_000,
                false
        );
        if (result == null) {
            player.sendMessage(SystemMessage.error("No trial chambers found nearby"));
            return null;
        }
        Location loc = result.getLocation();

        // Create explorer map via NMS
        net.minecraft.world.item.ItemStack nmsMap = MapItem.create(
                nmsWorld,
                loc.getBlockX(),
                loc.getBlockZ(),
                (byte) 2,
                true,
                true
        );

        MapItemSavedData mapData = MapItem.getSavedData(nmsMap, nmsWorld);
        if (mapData == null) {
            mapData = MapItemSavedData.createFresh(
                    loc.getBlockX(),
                    loc.getBlockZ(),
                    (byte) 2,
                    true,
                    true,
                    nmsWorld.dimension()
            );
            MapId mapId = nmsMap.get(DataComponents.MAP_ID);
            nmsWorld.setMapData(mapId, mapData);
        }

        // Render water as orange outlines
        MapItem.renderBiomePreviewMap(nmsWorld, nmsMap);

        // Add the Trial Chambers decoration
        Holder<MapDecorationType> decorationType = MapDecorationTypes.TRIAL_CHAMBERS;
        MapItemSavedData.addTargetDecoration(
                nmsMap,
                new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
                "Trial Chambers",
                decorationType
        );

        // Convert to Bukkit ItemStack and set display name
        ItemStack trialMap = CraftItemStack.asBukkitCopy(nmsMap);

        // Set dummy map details
        MapMeta mapMeta = (MapMeta) trialMap.getItemMeta();

        // Set the color
        mapMeta.setColor(Color.fromRGB(194, 107, 76));
        TextComponent mapName = Component.text("Trial Explorer Map").decoration(TextDecoration.ITALIC, false);

        mapMeta.lore(Arrays.asList(Component.text("A mysterious map that reveals the").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("way to the nearest Trial Chambers").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        mapMeta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        trialMap.setItemMeta(mapMeta);
        trialMap.setData(DataComponentTypes.ITEM_NAME, mapName);
        trialMap.setData(DataComponentTypes.RARITY, ItemRarity.UNCOMMON);

        return trialMap;
    }
}
