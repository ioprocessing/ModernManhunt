package org.ioprocessing.modernManhunt.menus;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.teams.ManhuntTeam;

import java.time.Duration;
import java.util.Set;

public final class UI {
    public static final ItemStack PREVIOUS_PAGE = createButton(Material.ARROW, "Previous Page", "left_button", null);
    public static final ItemStack NEXT_PAGE     = createButton(Material.ARROW, "Next Page", "right_button", null);
    public static final ItemStack BACK          = createButton(Material.ARROW, "Back", "left_button", null);
    public static final ItemStack EXIT          = createButton(Material.BARRIER, "Exit", "exit_button", null);
    public static final Sound CLICK_SOUND       = Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.UI, 1f, 1f);
    public static final Sound TELEPORT_SOUND    = Sound.sound(org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT, Sound.Source.UI, 1f, 1f);
    public static final Sound SUCCESS_SOUND     = Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, Sound.Source.UI, 1f, 1f);
    public static final Sound FAIL_SOUND        = Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO,  Sound.Source.UI, 1f, 0.9f);
    public static final Sound MANHUNT_END_SOUND = Sound.sound(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.UI, 1f, 1f);

    private static ItemStack createButton(Material mat, String name, String cmd, NamedTextColor color) {
        ItemStack item = new ItemStack(mat);

        item.setData(DataComponentTypes.CUSTOM_NAME,
                Component.text(name).decoration(TextDecoration.ITALIC, false).color(color != null ? color : NamedTextColor.WHITE));

        item.setData(DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplay.tooltipDisplay()
                        .hiddenComponents(Set.of(DataComponentTypes.ATTRIBUTE_MODIFIERS, DataComponentTypes.BUNDLE_CONTENTS))
                        .build());

        if (cmd != null) {
            CustomModelData customModelData = CustomModelData.customModelData()
                    .addString(cmd)
                    .build();
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
        }

        return item;
    }

    public static ItemStack createPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        NamedTextColor color = NamedTextColor.WHITE;

        ManhuntTeam team = Core.getInstance().getTeamManager().getPlayerTeam(player);

        if(team != null)
            switch (Core.getInstance().getTeamManager().getPlayerTeam(player).getType()) {
                case ManhuntTeam.TeamType.HUNTERS -> color = NamedTextColor.RED;
                case ManhuntTeam.TeamType.RUNNERS -> color = NamedTextColor.GREEN;
                case ManhuntTeam.TeamType.SPECTATORS -> color = NamedTextColor.BLUE;
            }

        meta.displayName(Component.text(player.getName(), color).decoration(TextDecoration.ITALIC, false));
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);

        return head;
    }

    public static ItemStack createToggleableButton(Material material, String name, String cmd, boolean enabled) {
        NamedTextColor color = enabled ?  NamedTextColor.GREEN : NamedTextColor.RED;
        String state = enabled ? "ENABLED" : "DISABLED";

        ItemStack button = createButton(material, name, cmd, color);
        button.setData(
                DataComponentTypes.LORE,
                ItemLore.lore()
                        .addLine(Component.text(state, color).decoration(TextDecoration.ITALIC, false))
                        .build()
        );

        return button;
    }

    public static Title teleportTitle() {
        Component mainTitle = Component.text("Spawn Location Set", NamedTextColor.AQUA).decoration(TextDecoration.BOLD, true);
        Component subTitle = Component.text("Teleporting in 5 seconds", NamedTextColor.GRAY);
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));

        return Title.title(mainTitle, subTitle, times);
    }

    public static Title huntersWinTitle() {
        Component mainTitle = Component.text("Hunters Win!", NamedTextColor.RED).decoration(TextDecoration.BOLD, true);
        Component subTitle = Component.text("", NamedTextColor.GRAY);
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));

        return Title.title(mainTitle, subTitle, times);
    }

    public static Title runnersWinTitle() {
        Component mainTitle = Component.text("Runners Win!", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true);
        Component subTitle = Component.text("", NamedTextColor.GRAY);
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));

        return Title.title(mainTitle, subTitle, times);
    }

    public static Title runnerDiesTitle() {
        Component mainTitle = Component.text("You Died", NamedTextColor.RED).decoration(TextDecoration.BOLD, true);
        Component subTitle = Component.text("You are now spectating", NamedTextColor.GRAY);
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));

        return Title.title(mainTitle, subTitle, times);
    }
}
