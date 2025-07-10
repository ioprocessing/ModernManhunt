package me.Ford.modernManhunt;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.*;

@ApiStatus.Experimental

public class EntityListener implements Listener {

    // Data tracking for death messages
    private final Map<UUID, ItemStack> lastHitWeapon = new HashMap<>();
    private final Set<UUID> lastHitProjectile = new HashSet<>();

    // Data tracking for custom bed bomb logic
    Location explosionLoc = null;

    public void GameEnd(String winnerMessage, NamedTextColor color) {
        // Call this when the game ends
        Component mainTitle = Component.text(winnerMessage, color).decoration(TextDecoration.BOLD, true);
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
        Title title = Title.title(mainTitle, Component.empty(), times);
        for (Player participant : ManhuntCommand.participantArray) {
            participant.showTitle(title);
            participant.playSound(participant.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.3f, 1f);
            participant.removeMetadata("DeadRunner", ModernManhunt.getInstance());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        {
            Player p = event.getEntity();
            if (p.getKiller() != null) {
                // If a hunter kills a hunter, prevent a head from dropping
                if (!(ManhuntCommand.mmHunters.getEntries().contains(p.getKiller().getName()) && ManhuntCommand.mmHunters.getEntries().contains(p.getName())))
                    event.getDrops().add(MMFunctions.ConsumablePlayerHead(p));
                boolean directHit = (p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                        || p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE);
                // If the death was caused directly by a projectile from a custom item,
                if(lastHitProjectile.contains(p.getUniqueId()) && lastHitWeapon.get(p.getUniqueId()).getPersistentDataContainer().has(Keys.CUSTOM_ITEM) && directHit) {
                    // Override the death message and prevent the custom item from displaying
                    event.deathMessage(Component.text()
                            .append(Component.text(p.getName()))
                            .append(Component.text(" was shot by "))
                            .append(Component.text(p.getKiller().getName()))
                            .build());
                } else if (directHit) /* If it was a melee kill,*/ {
                    ItemStack weapon = lastHitWeapon.get(p.getUniqueId());
                    // Override with custom message for primed pick and default for other custom items
                    if (weapon.getPersistentDataContainer().has(Keys.PRIMED_PICK)) {
                        event.deathMessage(Component.text()
                                .append(Component.text(p.getName()))
                                .append(Component.text(" was smelted by "))
                                .append(Component.text(p.getKiller().getName()))
                                .build());
                    } else if (weapon.getPersistentDataContainer().has(Keys.CUSTOM_ITEM)) {
                        event.deathMessage(Component.text()
                                .append(Component.text(p.getName()))
                                .append(Component.text(" was slain by "))
                                .append(Component.text(p.getKiller().getName()))
                                .build());
                    }
                }
            }
            for (ItemStack item : p.getInventory().getContents()) {
                if (item != null && item.getItemMeta().getPersistentDataContainer().has(Keys.HUNTER_COMPASS, PersistentDataType.BOOLEAN))
                    event.getDrops().remove(item);
            }
            if (p.hasMetadata("BeingHunted")) {
                boolean aliveRunner = false;
                p.removeMetadata("BeingHunted", ModernManhunt.getInstance());
                p.setMetadata("DeadRunner", new FixedMetadataValue(ModernManhunt.getInstance(), true));
                // If any of the players marked as runners are alive, don't end the game
                for (Player runner : ManhuntCommand.runnerArray) {
                    if (runner.hasMetadata("BeingHunted"))
                        aliveRunner = true;
                }
                if (!aliveRunner) {
                    GameEnd("Hunters win!", NamedTextColor.RED);
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.hasMetadata("DeadRunner")) {
                            MMFunctions.ExitSpectator(onlinePlayer);
                        }
                    }
                }
            }
            // Detecting if the explosion was caused by a bed-- not the cleanest way, but I can't find any better methods
            if (event.getDamageSource().getDamageType().equals(DamageType.EXPLOSION) && (event.getDamageSource().getCausingEntity() == null))
                event.deathMessage(Component.text()
                        .append(Component.text(p.getName()))
                        .append(Component.text(" was killed by [Intentional Game Design]"))
                        .build());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager().hasMetadata("DeadRunner"))
            e.setCancelled(true);
        if (e.getEntity() instanceof Player hit) {

            //If player is hit by a non-projectile
            if (e.getDamager() instanceof Player dmgr) {
                lastHitWeapon.put(hit.getUniqueId(), (dmgr.getInventory().getItemInMainHand()));
                lastHitProjectile.remove(hit.getUniqueId());
            }

            // If player is hit by a projectile
            if (e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player dmgr) {
                lastHitWeapon.put(hit.getUniqueId(), (dmgr.getInventory().getItemInMainHand()));
                lastHitProjectile.add(hit.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        // If the player right clicks with an item,
        if (p.hasMetadata("DeadRunner")) {
            if (event.getItem() == null || !event.getItem().getItemMeta().getPersistentDataContainer().has(Keys.SPECTATOR_COMPASS))
                event.setCancelled(true);
            else SpectatorGUI.Open(p);
        }
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
        /// NERF BED BOMBING ///
        if (event.getAction().isRightClick() && event.getClickedBlock() != null && event.getClickedBlock().getBlockData() instanceof Bed && ((p.getWorld().getEnvironment() == World.Environment.NETHER) || (p.getWorld().getEnvironment() == World.Environment.THE_END))) {
            event.setCancelled(true);
            Block bed = event.getClickedBlock();
            bed.setType(Material.AIR);
            // Register the explosion location to check for later
            explosionLoc = event.getClickedBlock().getLocation();
            bed.getWorld().createExplosion(explosionLoc, 3, true, true, null);
        }
    }

    @EventHandler
    public void onBedExplosion(EntityExplodeEvent event) {
        if ((explosionLoc != null) && (event.getLocation().equals(explosionLoc))) {
            event.setYield(1f/3f);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        PersistentDataContainer item = event.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer();
        // Check what item is being dropped
        if (item.has(Keys.HUNTER_COMPASS, PersistentDataType.BOOLEAN) || item.has(Keys.SPECTATOR_COMPASS, PersistentDataType.BOOLEAN))
            event.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player p =  event.getPlayer();
        if (p.hasMetadata("DeadRunner")) {
            MMFunctions.EnterSpectator(p);
            Bukkit.getScheduler().runTaskLater(ModernManhunt.getInstance(), () -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 0, false, false, false));
            }, 1L);
            p.give(MMFunctions.SpectatorCompass());
        }

        boolean aliveRunner = false;
        // If any of the players marked as runners are alive, give the compass (hunt is still on)
        for (Player runner : ManhuntCommand.runnerArray) {
            if (runner.hasMetadata("BeingHunted"))
                aliveRunner = true;
        }
        if (ManhuntCommand.mmHunters.getEntries().contains(event.getPlayer().getName()) && aliveRunner) {
            List<MetadataValue> metadata = p.getMetadata("TargetedPlayer");
            int index = metadata.getFirst().asInt();
            ItemStack compass = MMFunctions.HunterCompass(p);
            LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.get(index).getLocation(), false);
            compass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);
            p.getInventory().setItem(8, compass);
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        // If the player is a spectator, re-apply vanish and flight
        if (p.hasMetadata("DeadRunner")) {
            MMFunctions.EnterSpectator(p);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        // Cancel all damage dealt to spectators
        if (!(event.getEntity() instanceof Player))
            return;
        if (event.getEntity().hasMetadata("DeadRunner"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        // Cancel all damage dealt to spectators
        if (!(event.getEntity() instanceof Player))
            return;
        if (event.getEntity().hasMetadata("DeadRunner"))
            event.setCancelled(true);
    }
}
