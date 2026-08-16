package org.ioprocessing.modernManhunt.boons;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.BoonConfig;
import org.ioprocessing.modernManhunt.config.ConfigManager;
import org.ioprocessing.modernManhunt.items.ConsumableHeads;

import java.util.Map;

public class HeadBoon implements Boon {

    private final ConfigManager<BoonConfig> boon;

    public HeadBoon(Core core) {
        this.boon = core.getBoon();
    }

    @Override
    public BoonType getType() {
        return BoonType.HEAD;
    }

    @Override
    public long getDurationTicks() {
        return 0;
    }

    @Override
    public boolean isEnabled() {return boon.getConfig().getHead().isEnabled();}

    @Override
    public boolean isShowBossBar() {return false;}

    @Override
    public void apply(Player player, Map<String, Object> savedState) {
        Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
            player.give(ConsumableHeads.getConsumablePlayerHead(player));
        });
    }

    @Override
    public void reset(Player player, Map<String, Object> savedState) {
    }
}
