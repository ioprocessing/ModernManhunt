package org.ioprocessing.modernManhunt.boons;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.BoonConfig;
import org.ioprocessing.modernManhunt.config.ConfigManager;

import java.util.Map;

public class SpeedBoon implements Boon {

    private static final String KEY_ORIGINAL_SPEED = "originalSpeed";

    private final ConfigManager<BoonConfig> boon;

    public SpeedBoon(Core core) {
        this.boon = core.getBoon();
    }

    @Override
    public BoonType getType() {
        return BoonType.SPEED;
    }

    @Override
    public long getDurationTicks() {
        return getSpeedConfig().getDurationSeconds() * 20L;
    }

    @Override
    public boolean isEnabled() {return getSpeedConfig().isEnabled();}

    @Override
    public boolean isShowBossBar() {return getSpeedConfig().isShowBossBar();}

    @Override
    public void apply(Player player, Map<String, Object> savedState) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed == null) return;

        // Save current speed before we change anything
        savedState.put(KEY_ORIGINAL_SPEED, speed.getValue());
        speed.setBaseValue(getSpeedConfig().getSpeedAdjustment());
    }

    @Override
    public void reset(Player player, Map<String, Object> savedState) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed == null) return;

        // Restore whatever was saved or fall back to vanilla default
        double original = (double) savedState.getOrDefault(KEY_ORIGINAL_SPEED, 1.0);
        speed.setBaseValue(original);
    }

    private BoonConfig.Speed getSpeedConfig() {
        return boon.getConfig().getSpeed();
    }
}
