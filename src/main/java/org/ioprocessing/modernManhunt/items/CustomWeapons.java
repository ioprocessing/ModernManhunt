package org.ioprocessing.modernManhunt.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomWeapons implements Listener {

    /**
     * @return Bolstered Bow custom item
     */
    public static ItemStack getBolsteredBow() {
        ItemStack bolsterBow = new ItemStack(Material.BOW);
        ItemMeta meta = bolsterBow.getItemMeta();

        // Custom item properties
        TextComponent bowName = Component.text("Bolstered Bow", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.addEnchant(Enchantment.POWER, 1, false);
        meta.lore(Arrays.asList(
                Component.text("A powerful bow with an").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("increased draw weight").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("bolstered_bow"));
        meta.setCustomModelDataComponent(model);

        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        bolsterBow.setItemMeta(meta);
        bolsterBow.setData(DataComponentTypes.ITEM_NAME, bowName);

        return bolsterBow;
    }

    /**
     * @return Strengthened Sword custom item
     */
    public static ItemStack getStrengthenedSword() {
        ItemStack strengthSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = strengthSword.getItemMeta();

        // Custom item properties
        TextComponent swordName = Component.text("Strengthened Sword", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false);
        meta.addEnchant(Enchantment.SHARPNESS, 1, false);
        meta.lore(Arrays.asList(
                Component.text("An iron blade that has").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("been tempered and honed").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)));

        CustomModelDataComponent model = meta.getCustomModelDataComponent();
        model.setStrings(List.of("strengthened_sword"));
        meta.setCustomModelDataComponent(model);

        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        strengthSword.setItemMeta(meta);
        strengthSword.setData(DataComponentTypes.ITEM_NAME, swordName);

        return strengthSword;
    }

    ///
    /// DEATH MESSAGE MODIFICATION FOR CUSTOM WEAPONS/TOOLS
    ///

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

        EntityDamageEvent lastDamage = p.getLastDamageCause();
        if (lastDamage == null) return;

        boolean directHit = (lastDamage.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || lastDamage.getCause() == EntityDamageEvent.DamageCause.PROJECTILE);

        if (!directHit) return;

        ItemStack weapon = lastHitWeapon.get(p.getUniqueId());
        if (weapon == null || weapon.getType().isAir()) return;

        Player killer = p.getKiller();
        if (killer == null) return;

        boolean isProjectile = lastHitProjectile.contains(p.getUniqueId());

        if (isProjectile &&
                weapon.getPersistentDataContainer().has(CustomItems.CUSTOM_ITEM)) {

            e.deathMessage(Component.text()
                    .append(Component.text(p.getName()))
                    .append(Component.text(" was shot by "))
                    .append(Component.text(killer.getName()))
                    .build());

            return;
        }

        // Melee cases
        if (weapon.getPersistentDataContainer().has(PrimedPickaxe.PRIMED_PICKAXE_KEY)) {
            e.deathMessage(Component.text()
                    .append(Component.text(p.getName()))
                    .append(Component.text(" was smelted by "))
                    .append(Component.text(killer.getName()))
                    .build());

        } else if (weapon.getPersistentDataContainer().has(CustomItems.CUSTOM_ITEM)) {
            e.deathMessage(Component.text()
                    .append(Component.text(p.getName()))
                    .append(Component.text(" was slain by "))
                    .append(Component.text(killer.getName()))
                    .build());
        }
    }
}
