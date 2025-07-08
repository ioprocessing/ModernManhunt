package me.Ford.modernManhunt;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Worlds {
    private final static Worlds instance = new Worlds();

    private File file;
    private YamlConfiguration config;
    public static List<String> worldsList = new ArrayList<>();

    private Worlds() {
    }

    public void load() {
        file = new File(ModernManhunt.getInstance().getDataFolder(), "worlds.yml");

        if (!file.exists())
            ModernManhunt.getInstance().saveResource("worlds.yml", false);

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        worldsList = config.getStringList("Worlds");

    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(String world) {
        worldsList.add(world);
        config.set("Worlds", worldsList);
        save();
    }

    public void remove(String world) {
        worldsList.remove(world);
        config.set("Worlds", worldsList);
        save();
    }

    public static Worlds getInstance() {
        return instance;
    }
}
