package me.Ford.modernManhunt.PlayerListeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.Config;
import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.GUI.TeleporterGUI;
import me.Ford.modernManhunt.Keys;
import me.Ford.modernManhunt.ModernManhunt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

public class HunterListener implements Listener {

    Map<UUID, ItemStack[]> respawnArmor = new HashMap<>();

    @EventHandler
    public void onHunterDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (ManhuntCommand.hunterArray.contains(p)) {
            if (p.getKiller() != null) {
                // If a hunter kills a hunter, prevent a head from dropping
                if (!(ManhuntCommand.hunterArray.contains(p.getKiller()) && ManhuntCommand.hunterArray.contains(p)))
                    e.getDrops().add(CustomItems.consumablePlayerHead(p));
            }
            // If they're on the handicap armor list,
            if (Config.handicapArmorList.contains(p.getName())) {
                // Get all their drops and remove all the armor pieces they're wearing
                ItemStack[] armor = p.getInventory().getArmorContents();
                for (ItemStack armorPiece : armor) {
                    e.getDrops().remove(armorPiece);
                }
                // Add all armor pieces to map with the player ID
                respawnArmor.put(p.getUniqueId(), armor.clone());
            }
        }
    }


    @EventHandler
    public void onTPStar(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        // If they right-click with the TP star, open the handicap TP menu
        if (e.getItem() != null && e.getAction().isRightClick() && e.getItem().getItemMeta().getPersistentDataContainer().has(Keys.TP_STAR)) {
            e.setCancelled(true);
            TeleporterGUI.openHandicapTPGUI(p);
        }
    }

    @EventHandler
    public void onCompassUse(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        // If the player right clicks with an item,
        if (e.getItem() != null) {
            // Check if the item being clicked with has the 'HUNTER_COMPASS' key
            ItemMeta handItem = e.getItem().getItemMeta();
            if (e.getItem().getItemMeta().getPersistentDataContainer().has(Keys.HUNTER_COMPASS)) {
                // If it does, get the current targetable players
                List<MetadataValue> metadata = p.getMetadata("TargetedPlayer");
                int index = metadata.getFirst().asInt();

                // If the player left clicks with the compass
                if (e.getAction().isLeftClick()) {

                    NamedTextColor targetColor = NamedTextColor.GREEN;

                    // Increment the index whenever left-clicked, if at the end then reset
                    if (index < (ManhuntCommand.runnerArray.size() - 1))
                        index = index + 1;
                    else index = 0;

                    // Now set the new targeted player as the "next" in the list
                    p.setMetadata("TargetedPlayer", new FixedMetadataValue(ModernManhunt.getInstance(), index));

                    // Refresh "target" on the lore
                    handItem.lore(Arrays.asList(
                            Component.text()
                                    .append(Component.text("CURRENT TARGET: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                                    .append(Component.text(Functions.currentTarget(p).getName()).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                                    .build(),
                            Component.text("A compass that points at the runner(s).").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                            Component.text("Right click to refresh the location").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                            Component.text("and left click to change target.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
                    e.getItem().setItemMeta(handItem);

                    if (ManhuntCommand.runnerArray.get(index).getWorld().equals(p.getWorld())) {
                        LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.get(index).getLocation(), false);
                        e.getItem().setData(DataComponentTypes.LODESTONE_TRACKER, loc);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 0.5f);
                    } else {
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
                        targetColor = NamedTextColor.RED;
                    }

                    // Show who they're targeting above exp bar
                    p.sendActionBar(Component.text()
                            .append(Component.text("Targeting: ").color(targetColor))
                            .append(Component.text(Functions.currentTarget(p).getName()).color(targetColor))
                            .build());

                } else  /*If they right-click */ {
                    if (ManhuntCommand.runnerArray.get(index).getWorld().equals(p.getWorld())) {
                        LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.get(index).getLocation(), false);
                        e.getItem().setData(DataComponentTypes.LODESTONE_TRACKER, loc);
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.1f, 1f);
                        p.sendActionBar(Component.text()
                                .append(Component.text("Targeting: ").color(NamedTextColor.GREEN))
                                .append(Component.text(Functions.currentTarget(p).getName()).color(NamedTextColor.GREEN))
                                .build());
                    } else {
                        p.sendActionBar(Component.text("Target not found", NamedTextColor.RED));
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHunterRespawn(PlayerRespawnEvent e) {
        Player p =  e.getPlayer();
        // If the player is a handicapped runner, give them the TP star
        if (Config.handicapTPList.contains(p.getName()) && ManhuntCommand.hunterArray.contains(p))
            p.getInventory().setItem(7, CustomItems.tpStar());

        boolean aliveRunner = false;
        // If any of the players marked as runners are alive, give the respawning hunter a compass (hunt is still on)
        for (Player runner : ManhuntCommand.runnerArray) {
            if (runner.hasMetadata("BeingHunted"))
                aliveRunner = true;
        }
        if (ManhuntCommand.mmHunters.getEntries().contains(e.getPlayer().getName()) && aliveRunner) {
            List<MetadataValue> metadata = p.getMetadata("TargetedPlayer");
            int index = metadata.getFirst().asInt();
            ItemStack compass = CustomItems.hunterCompass(p);
            LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.get(index).getLocation(), false);
            compass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);
            p.getInventory().setItem(8, compass);
        }
        // If the player is marked to get respawn armor,
        ItemStack[] armor = respawnArmor.remove(p.getUniqueId());
        if (armor != null) {
            p.getInventory().setArmorContents(armor);
        }
    }
}
