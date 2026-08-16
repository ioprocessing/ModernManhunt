package org.ioprocessing.modernManhunt.menus;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.config.HandicapConfig;
import org.ioprocessing.modernManhunt.handicaps.HandicapType;
import org.ioprocessing.modernManhunt.items.TPStar;
import org.ioprocessing.modernManhunt.teams.TeamManager;

import java.util.List;

public class TPStarGui extends Gui {
    private final PaginationManager paginationManager = new PaginationManager(this);
    private final ConfigManager<HandicapConfig> handicap;
    private final Player clicker;

    public TPStarGui(Player player, Core core) {
        super(player, "tpstar-gui", MenuUtils.createOverlay(-8, "\uE137"), 4);
        paginationManager.registerPageSlotsBetween(0, 26);
        this.handicap = core.getHandicap();
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
        TeamManager teamManager = Core.getInstance().getTeamManager();
        List<Player> teammates = teamManager.isRunnerTeam(player) ? teamManager.getRunners() : teamManager.getHunters();
        for (Player teammate : teammates) {
            if(teammate != clicker)
                this.paginationManager.addItem(new Icon(UI.createPlayerHead(teammate)).onClick(e -> {
                    player.closeInventory();
                    player.teleport(teammate.getLocation());
                    player.playSound(UI.TELEPORT_SOUND);
                    player.sendMessage(SystemMessage.success(String.format("Teleported to {%s}", teammate.getName())));
                    Core.getInstance().getHandicapManager().applyCooldown(player, HandicapType.TEAMMATE_TP, TPStar.TP_STAR_COOLDOWN);
                }));
        }
    }

    private void updatePagination() {
        updatePlayers();
        paginationManager.update();
    }

}
