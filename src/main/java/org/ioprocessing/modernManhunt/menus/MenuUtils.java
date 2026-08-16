package org.ioprocessing.modernManhunt.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.Collections;

public class MenuUtils {
    private MenuUtils() {}

    /** Creates a player head item for modules to use */
    public static ItemStack createPlayerHead(Player player, boolean enlarge) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        // Define custom model data
        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(Collections.singletonList("player_head_display"));
        if (enlarge) meta.setCustomModelDataComponent(cmd);
        head.setItemMeta(meta);
        return head;
    }

    /** Creates overlay components for menu titles */
    public static Component createOverlay(int offset, String glyph) {
        return NegativeSpace.intToSpace(offset)
                .append(Component.text(glyph))
                .color(TextColor.color(255, 255, 255));
    }

    public static Component createOverlay(int offset, char glyph) {
        return createOverlay(offset, String.valueOf(glyph));
    }
}
