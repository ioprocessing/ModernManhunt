package me.Ford.modernManhunt;

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
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new PortalListener(), this);
        getServer().getPluginManager().registerEvents(new RespawnListener(), this);
        Objects.requireNonNull(getCommand("recipes")).setExecutor(new RecipesCommand());
        Objects.requireNonNull(getCommand("manhunt")).setExecutor(new ManhuntCommand());
        Worlds.getInstance().load();
        for (String worldName : Worlds.worldsList) {
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
                MMFunctions.ExitSpectator(p);
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
