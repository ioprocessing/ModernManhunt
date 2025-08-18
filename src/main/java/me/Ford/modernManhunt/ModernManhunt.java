package me.Ford.modernManhunt;

import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.Commands.RecipesCommand;
import me.Ford.modernManhunt.CustomItemListeners.HeadListener;
import me.Ford.modernManhunt.CustomItemListeners.PrimedPickaxeListener;
import me.Ford.modernManhunt.CustomItemListeners.SafetyListener;
import me.Ford.modernManhunt.CustomItems.CustomRecipes;
import me.Ford.modernManhunt.GUI.GUIListener;
import me.Ford.modernManhunt.OtherListeners.EnderDragonKillListener;
import me.Ford.modernManhunt.OtherListeners.PiglinBarterListener;
import me.Ford.modernManhunt.OtherListeners.StrengthListener;
import me.Ford.modernManhunt.PlayerListeners.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ModernManhunt extends JavaPlugin {

    public void unregisterCommands() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());

            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            // Store keys to remove after iteration
            List<String> toRemove = new ArrayList<>();

            for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
                Command cmd = entry.getValue();
                if (cmd instanceof PluginCommand pluginCmd && pluginCmd.getPlugin() == this) {
                    pluginCmd.unregister(commandMap);
                    toRemove.add(entry.getKey());
                }
            }

            // Now safely remove entries
            for (String key : toRemove) {
                knownCommands.remove(key);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register(Plugin instance) {
        CustomRecipes.register();
        Config.getInstance().load();
        getServer().getPluginManager().registerEvents(new HeadListener(), instance);
        getServer().getPluginManager().registerEvents(new PrimedPickaxeListener(), instance);
        getServer().getPluginManager().registerEvents(new SafetyListener(), instance);
        getServer().getPluginManager().registerEvents(new GUIListener(), instance);
        getServer().getPluginManager().registerEvents(new EnderDragonKillListener(), instance);
        getServer().getPluginManager().registerEvents(new DeathMessageListener(), instance);
        getServer().getPluginManager().registerEvents(new DimensionTravelListener(), instance);
        getServer().getPluginManager().registerEvents(new HunterListener(), instance);
        getServer().getPluginManager().registerEvents(new RespawnLocationListener(), instance);
        getServer().getPluginManager().registerEvents(new RunnerListener(), instance);
        getServer().getPluginManager().registerEvents(new SpectatorInteractionListener(), instance);
        getServer().getPluginManager().registerEvents(new MapUseListener(), instance);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), instance);
        if (Config.customBarteringEnabled)
            getServer().getPluginManager().registerEvents(new PiglinBarterListener(), instance);
        if (Config.strengthModifierEnabled)
            getServer().getPluginManager().registerEvents(new StrengthListener(), instance);
        if (Config.customBedBombingEnabled)
            getServer().getPluginManager().registerEvents(new BedBombingListener(), instance);
    }

    @Override
    public void onEnable() {
        register(this);
        Objects.requireNonNull(getCommand("recipes")).setExecutor(new RecipesCommand());
        Objects.requireNonNull(getCommand("manhunt")).setExecutor(new ManhuntCommand());
        for (String worldName : Config.worldsList) {
            World.Environment env = World.Environment.NORMAL;
            if (worldName.endsWith("_nether")) {env = World.Environment.NETHER;}
            if (worldName.endsWith("_the_end")) {env = World.Environment.THE_END;}
            Bukkit.createWorld(new WorldCreator(worldName).environment(env));
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            Functions.mmHunters.removePlayer(p);
            Functions.mmRunners.removePlayer(p);
            Functions.participantArray.remove(p);
            Functions.hunterArray.remove(p);
            Functions.runnerArray.remove(p);
        }

    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        unregisterCommands();
        Bukkit.getScheduler().cancelTasks(this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.removeMetadata("OpenedRecipesMenu", this);
            p.removeMetadata("OpenedSpectatorMenu", this);
            if (p.hasMetadata("DeadRunner")) {
                Functions.exitSpectator(p);
            }
            p.removeMetadata("BeingHunted", this);
            p.removeMetadata("TargetedPlayer", this);
            // ADD EVERY PLAYER METADATA CHANGE
        }
    }

    public static ModernManhunt getInstance() {
        return getPlugin(ModernManhunt.class);
    }
}
