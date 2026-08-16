package org.ioprocessing.modernManhunt.menus.handicap;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.HandicapConfig;
import org.ioprocessing.modernManhunt.handicaps.HandicapManager;
import org.ioprocessing.modernManhunt.handicaps.HandicapType;
import org.ioprocessing.modernManhunt.menus.MenuUtils;
import org.ioprocessing.modernManhunt.menus.UI;

import java.util.UUID;

public class HandicapSelectorGui extends Gui {

    private final Player clicker;
    private final UUID targetUUID;
    private final ConfigManager<HandicapConfig> handicap;

    public HandicapSelectorGui(Player player, UUID targetUUID) {
        super(player, "handicap-selector-gui", MenuUtils.createOverlay(-8, "\uE138"), 2);
        this.clicker = player;
        this.targetUUID = targetUUID;
        this.handicap = Core.getInstance().getHandicap();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

        addItem(0, new Icon(UI.BACK)
                .onClick(e -> {
                    new HandicapPlayerGui(clicker).open();
                    player.playSound(UI.CLICK_SOUND);
                }));

        addItem(8, new Icon(UI.EXIT)
                .onClick(e -> {
                    clicker.closeInventory();
                    player.playSound(UI.CLICK_SOUND);
                }));

        int slot = 9;
        HandicapManager handicapManager = Core.getInstance().getHandicapManager();

        for (HandicapType type : handicapManager.getRegistry().keySet()) {
            if(!handicapManager.isEnabled(type)) continue;
            addItem(slot++, new Icon(UI.createToggleableButton(
                    type.getMaterial(),
                    type.getLabel(),
                    null,
                    handicap.getConfig().isApplicable(targetUUID, type)))
                    .onClick(e -> toggle(type)));
        }
    }

    private void toggle(HandicapType type) {
        handicap.getConfig().toggle(targetUUID, type);
        Core.getInstance().getBoon().save();
        clicker.playSound(UI.CLICK_SOUND);
        new HandicapSelectorGui(clicker, targetUUID).open();
    }
}
