package me.Ford.modernManhunt.OtherListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class StrengthListener implements Listener {
    @EventHandler
    public void onStrengthHit(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player))
            return;
        Player p = (Player) e.getDamager();
        double damage = e.getDamage();
        if(p.hasPotionEffect(PotionEffectType.STRENGTH)) {
            Collection<PotionEffect> effects = p.getActivePotionEffects();
            for(PotionEffect effect : effects) {
                if(effect.getType() == PotionEffectType.STRENGTH) {
                    if(effect.getAmplifier() == 0) {
                        // Add 1 flat damage, multiplied by the cooldown of their attack
                        damage = damage + (1 * p.getAttackCooldown());
                    } else if (effect.getAmplifier() == 1) {
                        damage = damage + (2 * p.getAttackCooldown());
                    }
                }
            }
        }
        e.setDamage(damage);
    }
}
