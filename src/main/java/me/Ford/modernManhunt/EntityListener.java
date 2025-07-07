package me.Ford.modernManhunt;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@ApiStatus.Experimental

public class EntityListener implements Listener {

    public void GameEnd(String winnerMessage, NamedTextColor color) {
        Component mainTitle = Component.text(winnerMessage, color).decoration(TextDecoration.BOLD, true);
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
        Title title = Title.title(mainTitle, Component.empty(), times);
        for (Player participant : ManhuntCommand.participantArray) {
            participant.showTitle(title);
            participant.playSound(participant.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.3f, 1f);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        {
            Player p = event.getEntity();
            if (p.getKiller() != null) {
                ItemStack playerHead = MMFunctions.ConsumablePlayerHead(p);
                event.getDrops().add(playerHead);
            }
            for (ItemStack item : p.getInventory().getContents()) {
                if (item != null && item.getItemMeta().getPersistentDataContainer().has(Keys.HUNTER_COMPASS, PersistentDataType.BOOLEAN))
                    event.getDrops().remove(item);
            }
            if (p.hasMetadata("BeingHunted")) {
                boolean aliveRunner = false;
                p.removeMetadata("BeingHunted", ModernManhunt.getInstance());
                // If any of the players marked as runners are alive, don't end the game
                for (Player runner : ManhuntCommand.runnerArray) {
                    if (runner.hasMetadata("BeingHunted"))
                        aliveRunner = true;
                }
                if (!aliveRunner) {
                    GameEnd("Hunters win!", NamedTextColor.RED);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        // If the player right clicks with an item,
        if (event.getItem() != null) {
            // Check if the item being clicked with has the 'CONSUMABLE_HEAD' key
            ItemMeta handItem = event.getItem().getItemMeta();
            if (handItem.getPersistentDataContainer().has(Keys.CONSUMABLE_HEAD) && event.getAction().isRightClick() && event.getClickedBlock() == null) {

                // Then cancel the event and 'consume' the item
                event.setCancelled(true);
                PlayerHeads.consumePlayerHead(p, event.getItem());

            }
            // Separate effect for golden heads
            else if (handItem.getPersistentDataContainer().has(Keys.GOLDEN_HEAD) && event.getAction().isRightClick() && event.getClickedBlock() == null) {

                // Then cancel the event and 'consume' the item
                event.setCancelled(true);
                PlayerHeads.consumeGoldenHead(p, event.getItem());

            }
            // Hunter's compass interactions
            else if (handItem.getPersistentDataContainer().has(Keys.HUNTER_COMPASS)) {
                List<MetadataValue> metadata = p.getMetadata("TargetedPlayer");
                int index = metadata.getFirst().asInt();

                // If the player left clicks with the compass
                if (event.getAction().isLeftClick()) {

                    NamedTextColor targetColor = NamedTextColor.GREEN;

                    // Increment the index whenever left-clicked, if at the end then reset
                    if (index < (ManhuntCommand.runnerArray.size() - 1))
                        index = index + 1;
                    else index = 0;

                    p.setMetadata("TargetedPlayer", new FixedMetadataValue(ModernManhunt.getInstance(), index));

                    // Refresh "target" on the lore
                    handItem.lore(Arrays.asList(
                            Component.text()
                                    .append(Component.text("CURRENT TARGET: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                                    .append(Component.text(MMFunctions.CurrentTarget(p).getName()).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false))
                                    .build(),
                            Component.text("A compass that points at the runner(s).").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                            Component.text("Right click to refresh the location").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                            Component.text("and left click to change target.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));
                    event.getItem().setItemMeta(handItem);

                    // I should really make a function for this block of code
                    if (ManhuntCommand.runnerArray.get(index).getWorld().equals(p.getWorld())) {
                        LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.get(index).getLocation(), false);
                        event.getItem().setData(DataComponentTypes.LODESTONE_TRACKER, loc);
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 0.5f);
                    } else  {
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
                        targetColor = NamedTextColor.RED;
                    }

                    // Show who they're targeting above exp bar
                    p.sendActionBar(Component.text()
                            .append(Component.text("Targeting: ").color(targetColor))
                            .append(Component.text(MMFunctions.CurrentTarget(p).getName()).color(targetColor))
                            .build());

                } else  /*If they right-click */ {
                    // lmao
                    if (ManhuntCommand.runnerArray.get(index).getWorld().equals(p.getWorld())) {
                        LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.get(index).getLocation(), false);
                        event.getItem().setData(DataComponentTypes.LODESTONE_TRACKER, loc);
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.1f, 1f);
                        p.sendActionBar(Component.text()
                                .append(Component.text("Targeting: ").color(NamedTextColor.GREEN))
                                .append(Component.text(MMFunctions.CurrentTarget(p).getName()).color(NamedTextColor.GREEN))
                                .build());
                    } else {
                        p.sendActionBar(Component.text("Target not found", NamedTextColor.RED));
                        p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5f, 0.9f);
                    }
                }
            }
        }
        /// NERFED BED BOMBING ///
        if (event.getClickedBlock().getBlockData() instanceof Bed) {
            event.setCancelled(true);
            event.getClickedBlock().getWorld().createExplosion(event.getClickedBlock().getLocation(), 4, true, true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        // Check what item is being dropped
        if (item.getItemMeta().getPersistentDataContainer().has(Keys.HUNTER_COMPASS, PersistentDataType.BOOLEAN))
            event.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player p =  event.getPlayer();
        if (ManhuntCommand.mmHunters.getEntries().contains(event.getPlayer().getName())) {
            List<MetadataValue> metadata = p.getMetadata("TargetedPlayer");
            int index = metadata.getFirst().asInt();
            ItemStack compass = MMFunctions.HunterCompass(p);
            Bukkit.getScheduler().runTask(ModernManhunt.getInstance(), () -> {
                LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.get(index).getLocation(), false);
                compass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);
                event.getPlayer().give(compass);
            });
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity e = event.getEntity();
        if (e.getType().equals(EntityType.ENDER_DRAGON)) {
            boolean aliveRunner = false;
            // If any of the players marked as runners are alive, end the game
            for (Player runner : ManhuntCommand.runnerArray) {
                if (runner.hasMetadata("BeingHunted"))
                    aliveRunner = true;
            }
            if (aliveRunner) {
                GameEnd("Runners win!", NamedTextColor.GREEN);
                for (Player runner : ManhuntCommand.runnerArray) {runner.removeMetadata("BeingHunted",  ModernManhunt.getInstance());}
            }
        }
    }

    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent event) {
        // Register original barter loot
        List<ItemStack> originalLoot = event.getOutcome();

        // Clear and replace with custom drops
        originalLoot.clear();
        originalLoot.add(Bartering.getCustomDrop());
    }
}
