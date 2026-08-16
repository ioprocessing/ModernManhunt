package org.ioprocessing.modernManhunt.menus;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;

import java.util.List;

public class SpectatorGui extends Gui {
    private final PaginationManager paginationManager = new PaginationManager(this);
    private final Player clicker;

    public SpectatorGui(Player player) {
        super(player, "spectator-gui", MenuUtils.createOverlay(-8, "\uE137"), 4);
        paginationManager.registerPageSlotsBetween(0, 26);
        this.clicker = player;
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
        List<Player> participants = Core.getInstance().getTeamManager().getRunners();
        participants.addAll(Core.getInstance().getTeamManager().getHunters());
        for (Player participant : participants) {
            if(participant != clicker)
                this.paginationManager.addItem(new Icon(UI.createPlayerHead(participant)).onClick(e -> {
                    player.closeInventory();
                    player.teleport(participant.getLocation());
                    player.playSound(UI.TELEPORT_SOUND);
                    player.sendMessage(SystemMessage.success(String.format("Teleported to {%s}", participant.getName())));
                }));
        }
    }

    private void updatePagination() {
        updatePlayers();
        paginationManager.update();
    }
}
