package me.Ford.modernManhunt;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;

public class TeleporterGUI {
    public static void openSpec(Player p) {
        Inventory i = Bukkit.createInventory(p, 27, Component.text("Runners"));

        int count = 0;
        for (Player participant : ManhuntCommand.participantArray) {
            if(!participant.hasMetadata("DeadRunner")) {
                i.setItem(count, MMFunctions.getSpecHead(participant));
                count++;
            }
            // If for some reason you have over 27 alive players in the manhunt, stop filling the GUI. Seriously, don't do that
            if (count > 26)
                break;

        }

        p.openInventory(i);
        p.setMetadata("OpenedSpectatorMenu", new FixedMetadataValue(ModernManhunt.getInstance(),  "SpectatorMenu"));
    }

    public static void openTP(Player p) {
        Inventory i = Bukkit.createInventory(p, 27, Component.text("Hunters"));

        int count = 0;
        for (String hunter : ManhuntCommand.mmHunters.getEntries()) {
            if (Bukkit.getPlayer(hunter) == p)
                continue;
            i.setItem(count, MMFunctions.getSpecHead(Bukkit.getPlayer(hunter)));
            count++;
            // If for some reason you have over 27 alive players in the manhunt, stop filling the GUI. Seriously, don't do that
            if (count > 26)
                break;

        }

        p.openInventory(i);
        p.setMetadata("OpenedTPMenu", new FixedMetadataValue(ModernManhunt.getInstance(),  "TPMenu"));
    }
}
