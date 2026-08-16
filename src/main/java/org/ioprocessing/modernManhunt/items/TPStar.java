package org.ioprocessing.modernManhunt.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataType;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.menus.TPStarGui;

import java.util.List;

public class TPStar implements Listener {

    public static final NamespacedKey TP_STAR_KEY = new NamespacedKey(Core.getInstance(), "TPStar");
    public static final NamespacedKey TP_STAR_COOLDOWN = new NamespacedKey(Core.getInstance(), "TPStarCooldown");

    /**
     * @return TP Star custom item
     */
    public static ItemStack getTPStar() {
        ItemStack tpStar = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = tpStar.getItemMeta();

        // Custom item properties
        TextComponent tpStarName = Component.text("Teleportation Star", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false);

        String miniMsg = "<!i><white>USE <gray>to teleport to a teammate";
        Component message = MiniMessage.miniMessage().deserialize(miniMsg);

        meta.lore(List.of(message));

        meta.getPersistentDataContainer().set(TP_STAR_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.PREVENT_ITEM_DROP, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(CustomItems.CUSTOM_ITEM, PersistentDataType.BOOLEAN, true);

        UseCooldownComponent cd = meta.getUseCooldown();
        cd.setCooldownGroup(TP_STAR_COOLDOWN);

        tpStar.setItemMeta(meta);
        tpStar.setData(DataComponentTypes.ITEM_NAME, tpStarName);

        return tpStar;
    }

    /**
     * Open TP star GUI when player right-clicks with the nether star
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onStarUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(event.getAction().isLeftClick()) return;

        if(!CustomItems.hasKey(item, TPStar.TP_STAR_KEY)) return;

        if(player.getCooldown(TPStar.TP_STAR_COOLDOWN) != 0) {
            player.sendMessage(SystemMessage.error("On cooldown: " + player.getCooldown(TPStar.TP_STAR_COOLDOWN) / 20 + " seconds remaining"));
            return;
        }

        if(!Core.getInstance().getHandicap().getConfig().getTeammateTp().isEnabled()) {
            player.sendMessage(SystemMessage.warn("Teammate TP handicap is disabled in config"));
            return;
        }

        // If they right-click with the TP star compass, open the TP menu
        event.setCancelled(true);
        new TPStarGui(player, Core.getInstance()).open();
    }
}
