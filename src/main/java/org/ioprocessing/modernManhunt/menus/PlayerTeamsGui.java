package org.ioprocessing.modernManhunt.menus;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.gamestate.ManhuntState;
import org.ioprocessing.modernManhunt.teams.ManhuntTeam;
import org.ioprocessing.modernManhunt.teams.TeamManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerTeamsGui extends Gui {
    private final PaginationManager paginationManager = new PaginationManager(this);

    private final Map<UUID, Long> lastClick = new HashMap<>(); // Dedup for warning message when roaming too far to set spawn
    private static final long CLICK_COOLDOWN = 100;

    public PlayerTeamsGui(Player player) {
        super(player, "player-teams-gui", MenuUtils.createOverlay(-8, "\uE137"), 4);
        paginationManager.registerPageSlotsBetween(0, 26);
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

        for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
            // Modify head icon/lore
            ItemStack headIcon = UI.createPlayerHead(targetPlayer);
            ItemMeta meta = headIcon.getItemMeta();
            meta.lore(getTeamLore(targetPlayer));
            meta.customName(Component.text(targetPlayer.getName(), NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            headIcon.setItemMeta(meta);

            Icon icon = new Icon(headIcon);
            paginationManager.addItem(icon.onClick(e -> {
                UUID uuid = targetPlayer.getUniqueId();
                long now = System.currentTimeMillis();

                // Execute on a cooldown
                if (now - lastClick.getOrDefault(uuid, 0L) <= CLICK_COOLDOWN) return;

                if(Core.getInstance().isActiveManhunt() ||
                        Core.getInstance().getManhuntState() == ManhuntState.WAITING_FOR_TELEPORT) {
                    player.playSound(UI.FAIL_SOUND);
                    player.sendMessage(SystemMessage.error("Cannot change player's team during ongoing Manhunt"));
                    lastClick.put(uuid, now);
                    return;
                }

                player.playSound(UI.SUCCESS_SOUND);
                updatePlayerRole(targetPlayer);
                lastClick.put(uuid, now);
                updatePagination();
            }));
        }
    }

    private void updatePagination() {
        updatePlayers();
        paginationManager.update();
    }

    /** Cycles player between hunter -> runner -> spectator */
    private void updatePlayerRole(Player player) {
        TeamManager teamManager = Core.getInstance().getTeamManager();
        ManhuntTeam team = teamManager.getPlayerTeam(player);
        if (team == null) {
            teamManager.setHunterTeam(player);
            return;
        }

        switch (team.getType()) {
            case HUNTERS    -> teamManager.setRunnerTeam(player);
            case RUNNERS    -> teamManager.setSpectatorTeam(player);
            case SPECTATORS -> teamManager.setHunterTeam(player);
        }
    }

    private List<Component> getTeamLore(Player player) {
        TeamManager teamManager = Core.getInstance().getTeamManager();

        Component hunter = Component.text("HUNTER")
                .color(teamManager.isHunterTeam(player) ? NamedTextColor.RED : TextColor.color(0x7F5555))
                .decoration(TextDecoration.ITALIC, false);

        Component runner = Component.text("RUNNER")
                .color(teamManager.isRunnerTeam(player) ? NamedTextColor.GREEN : TextColor.color(0x557F55))
                .decoration(TextDecoration.ITALIC, false);

        Component spectator = Component.text("SPECTATOR")
                .color(teamManager.isSpectatorTeam(player) ? NamedTextColor.BLUE : TextColor.color(0x55557F))
                .decoration(TextDecoration.ITALIC, false);

        return List.of(hunter, runner, spectator);
    }
}
