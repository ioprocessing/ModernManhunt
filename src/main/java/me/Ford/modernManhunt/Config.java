package me.Ford.modernManhunt;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private final static Config instance = new Config();

    private File file;
    private YamlConfiguration config;
    public static List<String> worldsList = new ArrayList<>();
    public static List<String> handicapList = new ArrayList<>();
    public static Map<String, Boolean> recipeList = new HashMap<>();

    private Config() {
    }

    public void load() {
        file = new File(ModernManhunt.getInstance().getDataFolder(), "config.yml");

        if (!file.exists())
            ModernManhunt.getInstance().saveResource("config.yml", false);

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        worldsList = config.getStringList("Worlds");
        handicapList = config.getStringList("Handicap");

        // Load custom recipes
        ConfigurationSection recipeSection = config.getConfigurationSection("Custom Recipes");
        if (recipeSection != null) {
            for (String key : recipeSection.getKeys(false)) {
                recipeList.put(key, recipeSection.getBoolean(key));
            }
        }

    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(String value, String selector) {

        switch (selector){
            case "worlds" -> {
                worldsList.add(value);
                config.set("Worlds", worldsList);
            }
            case "handicap" -> {
                handicapList.add(value);
                config.set("Handicap",  handicapList);
            }
        }
        save();
    }

    public void remove(String value, String selector) {
        switch (selector){
            case "worlds" -> {
                worldsList.remove(value);
                config.set("Worlds", worldsList);
            }
            case "handicap" -> {
                handicapList.remove(value);
                config.set("Handicap",  handicapList);
            }
        }
        save();
    }

    public boolean isRecipeEnabled(String recipeName) {
        return recipeList.getOrDefault(recipeName, false);
    }

    public static Config getInstance() {
        return instance;
    }
}
