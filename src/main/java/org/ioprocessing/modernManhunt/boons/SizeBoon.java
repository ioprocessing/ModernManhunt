package org.ioprocessing.modernManhunt.boons;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.ioprocessing.modernManhunt.Core;
import org.ioprocessing.modernManhunt.config.BoonConfig;
import org.ioprocessing.modernManhunt.config.ConfigManager;

import java.util.Map;

public class SizeBoon implements Boon {

    private static final String KEY_ORIGINAL_SCALE = "originalScale";

    private final ConfigManager<BoonConfig> boon;

    public SizeBoon(Core core) {
        this.boon = core.getBoon();
    }

    @Override
    public BoonType getType() {
        return BoonType.SIZE;
    }

    @Override
    public long getDurationTicks() {
        return getSizeConfig().getDurationSeconds() * 20L;
    }

    @Override
    public boolean isEnabled() {return getSizeConfig().isEnabled();}

    @Override
    public boolean isShowBossBar() {return getSizeConfig().isShowBossBar();}

    @Override
    public void apply(Player player, Map<String, Object> savedState) {
        AttributeInstance scale = player.getAttribute(Attribute.SCALE);
        if (scale == null) return;

        // Save current scale before we change anything
        savedState.put(KEY_ORIGINAL_SCALE, scale.getValue());
        scale.setBaseValue(getSizeConfig().getSizeAdjustment());
    }

    @Override
    public void reset(Player player, Map<String, Object> savedState) {
        AttributeInstance scale = player.getAttribute(Attribute.SCALE);
        if (scale == null) return;

        // Restore whatever was saved or fall back to vanilla default
        double original = (double) savedState.getOrDefault(KEY_ORIGINAL_SCALE, 1.0);
        scale.setBaseValue(original);
    }

    private BoonConfig.Size getSizeConfig() {
        return boon.getConfig().getSize();
    }
}
