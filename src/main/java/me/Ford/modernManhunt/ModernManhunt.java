package me.Ford.modernManhunt;

import me.Ford.modernManhunt.Commands.ManhuntCommand;
import me.Ford.modernManhunt.Commands.RecipesCommand;
import me.Ford.modernManhunt.CustomItemListeners.HeadListener;
import me.Ford.modernManhunt.CustomItemListeners.PrimedPickaxeListener;
import me.Ford.modernManhunt.CustomItemListeners.SafetyListener;
import me.Ford.modernManhunt.CustomItems.CustomRecipes;
import me.Ford.modernManhunt.GUI.GUIListener;
import me.Ford.modernManhunt.OtherListeners.EntityListener;
import me.Ford.modernManhunt.PlayerListeners.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ModernManhunt extends JavaPlugin {

    private void unregisterCommands() {
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

    @Override
    public void onEnable() {
        CustomRecipes.register();
        getServer().getPluginManager().registerEvents(new HeadListener(), this);
        getServer().getPluginManager().registerEvents(new PrimedPickaxeListener(), this);
        getServer().getPluginManager().registerEvents(new SafetyListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new BedBombingListener(), this);
        getServer().getPluginManager().registerEvents(new DeathMessageListener(), this);
        getServer().getPluginManager().registerEvents(new DimensionTravelListener(), this);
        getServer().getPluginManager().registerEvents(new HunterListener(), this);
        getServer().getPluginManager().registerEvents(new RespawnEffectListener(), this);
        getServer().getPluginManager().registerEvents(new RespawnLocationListener(), this);
        getServer().getPluginManager().registerEvents(new RunnerListener(), this);
        getServer().getPluginManager().registerEvents(new SpectatorInteractionListener(), this);
        Objects.requireNonNull(getCommand("recipes")).setExecutor(new RecipesCommand());
        Objects.requireNonNull(getCommand("manhunt")).setExecutor(new ManhuntCommand());
        Config.getInstance().load();
        for (String worldName : Config.worldsList) {
            World.Environment env = World.Environment.NORMAL;
            if (worldName.endsWith("_nether")) {env = World.Environment.NETHER;}
            if (worldName.endsWith("_the_end")) {env = World.Environment.THE_END;}
            Bukkit.createWorld(new WorldCreator(worldName).environment(env));
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
