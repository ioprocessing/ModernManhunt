package org.ioprocessing.modernManhunt.menus.boon;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.boons.BoonManager;
import org.ioprocessing.modernManhunt.boons.BoonType;
import org.ioprocessing.modernManhunt.config.BoonConfig;
import org.ioprocessing.modernManhunt.menus.MenuUtils;
import org.ioprocessing.modernManhunt.menus.UI;

import java.util.UUID;

public class BoonSelectorGui extends Gui {

    private final Player clicker;
    private final UUID targetUUID;
    private final BoonConfig boon;

    public BoonSelectorGui(Player player, UUID targetUUID) {
        super(player, "boon-selector-gui", MenuUtils.createOverlay(-8, "\uE138"), 2);
        this.clicker = player;
        this.targetUUID = targetUUID;
        this.boon = Core.getInstance().getBoon().getConfig();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        addItem(0, new Icon(UI.BACK).onClick(e -> {
            new BoonPlayerGui(clicker).open();
            player.playSound(UI.CLICK_SOUND);
        }));

        addItem(8, new Icon(UI.EXIT).onClick(e -> {
            clicker.closeInventory();
            player.playSound(UI.CLICK_SOUND);
        }));

        int slot = 9;
        BoonManager boonManager = Core.getInstance().getBoonManager();

        for (BoonType type : boonManager.getRegistry().keySet()) {
            if(!boonManager.isEnabled(type)) continue;
            addItem(slot++, new Icon(UI.createToggleableButton(
                    type.getMaterial(),
                    type.getLabel(),
                    null,
                    boon.isApplicable(targetUUID, type)))
                    .onClick(e -> toggle(type)));
        }
    }

    private void toggle(BoonType type) {
        boon.toggle(targetUUID, type);
        Core.getInstance().getBoon().save();
        clicker.playSound(UI.CLICK_SOUND);
        new BoonSelectorGui(clicker, targetUUID).open();
    }
}
