package org.ioprocessing.modernManhunt.miscellaneous.potions;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.CoreConfig;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class StrengthPotion implements Listener {
    private final ConfigManager<CoreConfig> coreConfig;

    public StrengthPotion(Core plugin) {
        this.coreConfig = plugin.getCoreConfig();
    }

    // Damage adjustment
    @EventHandler
    public void onStrengthHit(EntityDamageByEntityEvent e) {
        CoreConfig.PotionAdjustments potionAdjustments = coreConfig.getConfig().getPotionAdjustments();
        if(!(e.getDamager() instanceof Player p) || !(potionAdjustments.isEnabled()))
            return;
        double damage = e.getDamage();
        if(p.hasPotionEffect(PotionEffectType.STRENGTH)) {
            Collection<PotionEffect> effects = p.getActivePotionEffects();
            for(PotionEffect effect : effects) {
                if(effect.getType() == PotionEffectType.STRENGTH) {
                    int strengthLevel = effect.getAmplifier() + 1;
                    float customDamageIncrease = potionAdjustments.getStrengthDamageBoostPerLevel() * strengthLevel * p.getAttackCooldown();
                    // Original damage factors in vanilla strength, so we adjust by adding the custom amount and subtracting vanilla
                        damage = damage + (customDamageIncrease // Add custom strength buff
                                        - (3 * (strengthLevel * p.getAttackCooldown()))); // Subtract vanilla strength buff
                }
            }
        }
        e.setDamage(damage);
    }

    // Potion tooltip
    @EventHandler
    public void onBrew(BrewEvent event) {
        List<ItemStack> contents = event.getResults();
        for (ItemStack potion : contents) {
            if (potion == null) continue;
            applyStrengthTooltip(potion);
        }
    }

    private void applyStrengthTooltip(ItemStack item) {
        if (item.getType() != Material.POTION
                && item.getType() != Material.SPLASH_POTION
                && item.getType() != Material.LINGERING_POTION) return;

        PotionContents contents = item.getData(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return;

        PotionType type = contents.potion();
        if (type == null) return;

        for(PotionEffect effect : contents.allEffects()) {
            if(effect.getType() == PotionEffectType.STRENGTH) {
                addStrengthPotionLore(item, effect);
                return;
            }
        }
    }

    private void addStrengthPotionLore(ItemStack potion, PotionEffect effect) {
        int damageBuff = (effect.getAmplifier() + 1) * Core.getInstance()
                                                .getCoreConfig()
                                                .getConfig()
                                                .getPotionAdjustments()
                                                .getStrengthDamageBoostPerLevel();
        int durationTicks = effect.getDuration();
        String duration = String.format("(%02d:%02d)", durationTicks / 1200, (durationTicks / 20) % 60);

        // Suppress the vanilla potion effect tooltip section
        potion.setData(DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplay.tooltipDisplay()
                        .hiddenComponents(Set.of(DataComponentTypes.POTION_CONTENTS))
                        .build());

        // Replace it with corrected lore
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.lore(List.of(
                Component.text("Strength " + duration, NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false),
                Component.empty(),
                Component.text("When Applied:").decoration(TextDecoration.ITALIC, false),
                Component.text("+" + damageBuff + " Attack Damage", NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
        ));
        potion.setItemMeta(meta);
    }
}
