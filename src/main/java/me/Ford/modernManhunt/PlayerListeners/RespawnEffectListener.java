package me.Ford.modernManhunt.PlayerListeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.Config;
import me.Ford.modernManhunt.CustomItems.CustomItems;
import me.Ford.modernManhunt.Functions;
import me.Ford.modernManhunt.ModernManhunt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class RespawnEffectListener implements Listener {
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player p =  event.getPlayer();
        // If the player is a handicapped runner, give them the TP star
        if (Config.handicapList.contains(p.getName()) && ManhuntCommand.mmHunters.getEntries().contains(p.getName()))
            p.getInventory().setItem(7, CustomItems.tpStar());
        // If the respawning player is a runner who died, put them in spectator mode and give them the compass
        if (p.hasMetadata("DeadRunner")) {
            Functions.enterSpectator(p);
            Bukkit.getScheduler().runTaskLater(ModernManhunt.getInstance(), () -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0, false, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 0, false, false, false));
            }, 1L);
            p.give(CustomItems.spectatorCompass());
        }

        boolean aliveRunner = false;
        // If any of the players marked as runners are alive, give the respawning hunter a compass (hunt is still on)
        for (Player runner : ManhuntCommand.runnerArray) {
            if (runner.hasMetadata("BeingHunted"))
                aliveRunner = true;
        }
        if (ManhuntCommand.mmHunters.getEntries().contains(event.getPlayer().getName()) && aliveRunner) {
            List<MetadataValue> metadata = p.getMetadata("TargetedPlayer");
            int index = metadata.getFirst().asInt();
            ItemStack compass = CustomItems.hunterCompass(p);
            LodestoneTracker loc = LodestoneTracker.lodestoneTracker(ManhuntCommand.runnerArray.get(index).getLocation(), false);
            compass.setData(DataComponentTypes.LODESTONE_TRACKER, loc);
            p.getInventory().setItem(8, compass);
        }
    }
}
