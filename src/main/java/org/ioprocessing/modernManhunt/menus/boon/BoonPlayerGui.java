package org.ioprocessing.modernManhunt.menus.boon;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.ioprocessing.modernManhunt.menus.MenuUtils;
import org.ioprocessing.modernManhunt.menus.UI;

public class BoonPlayerGui extends Gui {
    private final PaginationManager paginationManager = new PaginationManager(this);
    private final Player player;

    public BoonPlayerGui(Player player) {
        super(player, "boon-player-gui", MenuUtils.createOverlay(-8, "\uE137"), 4);
        paginationManager.registerPageSlotsBetween(0, 26);
        this.player = player;
    }

    // Initialize Gui
    @Override
    public void onOpen(InventoryOpenEvent event) {
        updatePagination();

        if (paginationManager.getCurrentPage() != 0) {
            addItem(27, new Icon(UI.PREVIOUS_PAGE).onClick(e -> {
                paginationManager.goPreviousPage();
                player.playSound(UI.CLICK_SOUND);
                updatePagination();
            }));
        }
        if (!paginationManager.isLastPage()) {
            addItem(35, new Icon(UI.NEXT_PAGE).onClick(e -> {
                paginationManager.goNextPage();
                player.playSound(UI.CLICK_SOUND);
                updatePagination();
            }));
        }
    }

    private void updatePlayers() {
        paginationManager.getItems().clear();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            this.paginationManager.addItem(new Icon(UI.createPlayerHead(onlinePlayer)).onClick(e -> {
                player.playSound(UI.CLICK_SOUND);
                new BoonSelectorGui(player, onlinePlayer.getUniqueId()).open();
            }));
        }
    }

    private void updatePagination() {
        updatePlayers();
        paginationManager.update();
    }
}
