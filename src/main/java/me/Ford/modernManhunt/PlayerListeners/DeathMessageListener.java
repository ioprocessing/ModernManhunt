package me.Ford.modernManhunt.PlayerListeners;

import me.Ford.modernManhunt.Keys;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DeathMessageListener implements Listener {

    // Data tracking for death messages
    private final Map<UUID, ItemStack> lastHitWeapon = new HashMap<>();
    private final Set<UUID> lastHitProjectile = new HashSet<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player hit) {

            //If player is hit by a non-projectile, record the melee weapon used
            if (e.getDamager() instanceof Player damager) {
                lastHitWeapon.put(hit.getUniqueId(), (damager.getInventory().getItemInMainHand()));
                lastHitProjectile.remove(hit.getUniqueId());
            }

            // If player is hit by a projectile, record the shooter's weapon and mark it as projectile damage
            if (e.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player damager) {
                lastHitWeapon.put(hit.getUniqueId(), (damager.getInventory().getItemInMainHand()));
                lastHitProjectile.add(hit.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        boolean directHit = (p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE);
        // If the death was caused directly by a projectile from a custom item,
        if(lastHitProjectile.contains(p.getUniqueId()) && lastHitWeapon.get(p.getUniqueId()).getPersistentDataContainer().has(Keys.CUSTOM_ITEM) && directHit) {
            // Override the death message and prevent the custom item from displaying
            e.deathMessage(Component.text()
                    .append(Component.text(p.getName()))
                    .append(Component.text(" was shot by "))
                    .append(Component.text(p.getKiller().getName()))
                    .build());
        } else if (directHit) /* If it was a melee kill, */ {
            ItemStack weapon = lastHitWeapon.get(p.getUniqueId());
            // Override with custom message for primed pick and default for other custom items
            if (weapon.getPersistentDataContainer().has(Keys.PRIMED_PICK)) {
                e.deathMessage(Component.text()
                        .append(Component.text(p.getName()))
                        .append(Component.text(" was smelted by "))
                        .append(Component.text(p.getKiller().getName()))
                        .build());
            } else if (weapon.getPersistentDataContainer().has(Keys.CUSTOM_ITEM)) {
                e.deathMessage(Component.text()
                        .append(Component.text(p.getName()))
                        .append(Component.text(" was slain by "))
                        .append(Component.text(p.getKiller().getName()))
                        .build());
            }
        }
    }
}
