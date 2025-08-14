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
    public static List<String> handicapTPList = new ArrayList<>();
    public static List<String> handicapArmorList = new ArrayList<>();
    public static Map<String, Integer> cooldownList = new HashMap<>();
    public static Map<String, Boolean> recipeList = new HashMap<>();
    public static Map<String, Integer> barterWeightList = new HashMap<>();
    public static Map<String, Integer> strengthModifierList = new HashMap<>();
    public static boolean customBarteringEnabled;
    public static boolean strengthModifierEnabled;
    public static boolean customBedBombingEnabled;
    public static int bedExplosionStrength;
    public static int netherTravelRatio;

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
        handicapTPList = config.getStringList("HandicapTP");
        handicapArmorList = config.getStringList("HandicapArmor");
        customBarteringEnabled = config.getBoolean("Custom Bartering");
        strengthModifierEnabled = config.getBoolean("Custom Strength Modifier");
        customBedBombingEnabled = config.getBoolean("Custom Bed Bombing");
        bedExplosionStrength = config.getInt("Bed Bombing Explosion Strength");
        netherTravelRatio = config.getInt("Nether Travel Ratio");
        // Load cooldown durations
        ConfigurationSection cooldownSection = config.getConfigurationSection("Cooldowns");
        if (cooldownSection != null) {
            for (String key : cooldownSection.getKeys(false))
                cooldownList.put(key, cooldownSection.getInt(key));
        }

        // Load custom recipes
        ConfigurationSection recipeSection = config.getConfigurationSection("Custom Recipes");
        if (recipeSection != null) {
            for (String key : recipeSection.getKeys(false)) {
                recipeList.put(key, recipeSection.getBoolean(key));
            }
        }

        // Load custom bartering weights
        ConfigurationSection barteringSection = config.getConfigurationSection("Custom Bartering Weights");
        if (barteringSection != null) {
            for (String key : barteringSection.getKeys(false)) {
                barterWeightList.put(key, barteringSection.getInt(key));
            }
        }

        // Load strength damage modifiers
        ConfigurationSection strengthSection = config.getConfigurationSection("Custom Strength Modifiers");
        if (strengthSection != null) {
            for (String key : strengthSection.getKeys(false)) {
                strengthModifierList.put(key, strengthSection.getInt(key));
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
            case "handicapTP" -> {
                handicapTPList.add(value);
                config.set("HandicapTP", handicapTPList);
            }
            case "handicapArmor" -> {
                handicapArmorList.add(value);
                config.set("HandicapArmor", handicapArmorList);
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
            case "handicapTP" -> {
                handicapTPList.remove(value);
                config.set("HandicapTP", handicapTPList);
            }
            case "handicapArmor" -> {
                handicapArmorList.remove(value);
                config.set("HandicapArmor", handicapArmorList);
            }
        }
        save();
    }

    public boolean isRecipeEnabled(String recipeName) {
        return recipeList.getOrDefault(recipeName, false);
    }

    public int itemCooldown(String itemName) {
        return cooldownList.getOrDefault(itemName, 0);
    }

    public static int barterWeight(String barterName) {
        return barterWeightList.getOrDefault(barterName, 0);
    }

    public int strengthModifier(String strengthLevel) {
        return strengthModifierList.getOrDefault(strengthLevel, 0);
    }

    public static Config getInstance() {
        return instance;
    }
}
