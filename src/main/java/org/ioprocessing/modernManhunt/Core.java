package org.ioprocessing.modernManhunt;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import mc.obliviate.inventory.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.ioprocessing.modernManhunt.boons.BoonManager;
import org.ioprocessing.modernManhunt.boons.HeadBoon;
import org.ioprocessing.modernManhunt.boons.SizeBoon;
import org.ioprocessing.modernManhunt.boons.SpeedBoon;
import org.ioprocessing.modernManhunt.commands.ModernManhuntCommand;
import org.ioprocessing.modernManhunt.config.*;
import org.ioprocessing.modernManhunt.gamestate.GameStateListener;
import org.ioprocessing.modernManhunt.gamestate.ManhuntManager;
import org.ioprocessing.modernManhunt.gamestate.ManhuntState;
import org.ioprocessing.modernManhunt.handicaps.*;
import org.ioprocessing.modernManhunt.items.*;
import org.ioprocessing.modernManhunt.miscellaneous.bartering.PiglinBartering;
import org.ioprocessing.modernManhunt.miscellaneous.bedbombing.BedBombing;
import org.ioprocessing.modernManhunt.miscellaneous.dimensions.DimensionalRespawn;
import org.ioprocessing.modernManhunt.miscellaneous.dimensions.DimensionalTravel;
import org.ioprocessing.modernManhunt.miscellaneous.potions.StrengthPotion;
import org.ioprocessing.modernManhunt.teams.Hunter;
import org.ioprocessing.modernManhunt.teams.Runner;
import org.ioprocessing.modernManhunt.teams.Spectator;
import org.ioprocessing.modernManhunt.teams.TeamManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public final class Core extends JavaPlugin {

    public static final String MANHUNT_NAMESPACE = "manhunt";
    public static final NamespacedKey MANHUNT_OVERWORLD = new NamespacedKey(MANHUNT_NAMESPACE, "overworld");
    public static final NamespacedKey MANHUNT_NETHER = new NamespacedKey(MANHUNT_NAMESPACE, "the_nether");
    public static final NamespacedKey MANHUNT_THE_END = new NamespacedKey(MANHUNT_NAMESPACE, "the_end");

    private ConfigManager<CoreConfig> coreConfig;
    private ConfigManager<BarterPoolConfig> bartering;
    private ConfigManager<HandicapConfig> handicap;
    private ConfigManager<BoonConfig> boon;

    @Getter
    private TeamManager teamManager;

    @Getter
    private ManhuntManager manhuntManager;

    @Getter
    private BoonManager boonManager;

    @Getter
    private HandicapManager handicapManager;

    @Getter
    private static Core instance;

    @Getter
    public static List<World> manhuntWorlds = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        coreConfig = new ConfigManager<>("config.json", CoreConfig.class);
        bartering = new ConfigManager<>("bartering.json", BarterPoolConfig.class);
        handicap = new ConfigManager<>("handicap.json", HandicapConfig.class);
        boon = new ConfigManager<>("boon.json", BoonConfig.class);

        coreConfig.load();
        bartering.load();
        handicap.load();
        boon.load();

        // IventoryAPI dependency
        new InventoryAPI(this).init();

        // Team manager for hunters/runners
        teamManager = new TeamManager(this);

        // Manhunt manager for gamestate
        manhuntManager = new ManhuntManager(this);

        // Boon manager
        boonManager = new BoonManager(this);

        // Handicap manager
        handicapManager = new HandicapManager(this);

        registerListeners();
        registerHandicaps();
        registerCommands();
        registerBoons();
        loadWorlds();

        logger().info("ModernManhunt enabled");
    }

    @Override
    public void onDisable() {
        logger().info("ModernManhunt disabled");
    }

    public static org.slf4j.Logger logger() { return Core.getInstance().getSLF4JLogger(); }

    private void registerListeners() {
        // Custom bartering system
        Bukkit.getPluginManager().registerEvents(new PiglinBartering(this), this);

        // Strength potion adjustment
        Bukkit.getPluginManager().registerEvents(new StrengthPotion(this), this);

        // Bed bombing adjustment
        Bukkit.getPluginManager().registerEvents(new BedBombing(this), this);

        // Dimensional travel
        Bukkit.getPluginManager().registerEvents(new DimensionalTravel(this), this);
        Bukkit.getPluginManager().registerEvents(new DimensionalRespawn(), this);

        // Ender pearl cooldown
        Bukkit.getPluginManager().registerEvents(new EnderPearl(this), this);

        // Custom item listeners
        Bukkit.getPluginManager().registerEvents(new PrimedPickaxe(), this);
        Bukkit.getPluginManager().registerEvents(new ConsumableHeads(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomItems(), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorCompass(), this);
        Bukkit.getPluginManager().registerEvents(new HunterCompass(), this);
        Bukkit.getPluginManager().registerEvents(new TrialChamberMap(), this);
        Bukkit.getPluginManager().registerEvents(new TPStar(), this);
        Bukkit.getPluginManager().registerEvents(new TeammateTPHandicap(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomWeapons(), this);
        Bukkit.getPluginManager().registerEvents(new FortressCompass(), this);

        // Manhunt listeners
        Bukkit.getPluginManager().registerEvents(new Hunter(this), this);
        Bukkit.getPluginManager().registerEvents(new Runner(this), this);
        Bukkit.getPluginManager().registerEvents(new Spectator(), this);
        Bukkit.getPluginManager().registerEvents(new GameStateListener(this), this);
        Bukkit.getPluginManager().registerEvents(boonManager, this);
    }

    private void registerCommands() {
        // Register /mm
        ModernManhuntCommand modernManhuntCommand = new ModernManhuntCommand(this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, modernManhuntCommand::register);
    }

    public void registerBoons() {
        boonManager.register(new SizeBoon(this));
        boonManager.register(new SpeedBoon(this));
        boonManager.register(new HeadBoon(this));
    }

    public void registerHandicaps() {
        KeepArmorHandicap keepArmorHandicap = new KeepArmorHandicap(this);
        KeepHotbarHandicap keepHotbarHandicap = new KeepHotbarHandicap(this);
        KeepInventoryHandicap keepInventoryHandicap = new KeepInventoryHandicap(this);
        FortressCompassHandicap fortressCompassHandicap = new FortressCompassHandicap(this);

        Bukkit.getPluginManager().registerEvents(keepArmorHandicap, this);
        handicapManager.register(keepArmorHandicap);

        Bukkit.getPluginManager().registerEvents(keepHotbarHandicap, this);
        handicapManager.register(keepHotbarHandicap);

        Bukkit.getPluginManager().registerEvents(keepInventoryHandicap, this);
        handicapManager.register(keepInventoryHandicap);

        Bukkit.getPluginManager().registerEvents(fortressCompassHandicap, this);
        handicapManager.register(fortressCompassHandicap);

        handicapManager.register(new TeammateTPHandicap(this));
        handicapManager.register(new ExactCoordsHandicap(this));
    }

    private void loadWorlds() {
        Map<NamespacedKey, World.Environment> dimensions = Map.of(
                MANHUNT_OVERWORLD, World.Environment.NORMAL,
                MANHUNT_NETHER,    World.Environment.NETHER,
                MANHUNT_THE_END,   World.Environment.THE_END
        );
        for (Map.Entry<NamespacedKey, World.Environment> entry : dimensions.entrySet()) {
            if (Bukkit.getWorld(entry.getKey()) == null) {
                // Check if the dimension folder exists on disk before attempting to load
                File dimFolder = new File(Bukkit.getWorldContainer(),
                        "world/dimensions/" + entry.getKey().getNamespace() + "/" + entry.getKey().getKey());
                if (dimFolder.exists()) {
                    WorldCreator.ofKey(entry.getKey())
                            .environment(entry.getValue())
                            .createWorld();
                    Core.logger().info("Loaded existing manhunt dimension: {}", entry.getKey());
                }
            }
        }
    }

    public boolean isActiveManhunt() {
        ManhuntState state = manhuntManager.getState();
        return (state == ManhuntState.RUNNING || state == ManhuntState.STARTING || state == ManhuntState.WAITING_FOR_DELAY);
    }

    public ManhuntState getManhuntState() {
        return manhuntManager.getState();
    }
}
