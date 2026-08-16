package org.ioprocessing.modernManhunt.config;

import org.ioprocessing.modernManhunt.Core;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager<T> {

    private final Path configPath;
    private final GsonConfigurationLoader loader;
    private final Class<T> configClass;

    @Getter
    private T config;

    public ConfigManager(String fileName, Class<T> configClass) {
        this.configClass = configClass;

        Path manhuntRoot = Bukkit.getPluginsFolder().toPath()
                .resolve("ModernManhunt");

        this.configPath = manhuntRoot.resolve(fileName);

        this.loader = GsonConfigurationLoader.builder()
                .path(configPath)
                .build();
    }

    public void load() {
        try {
            Files.createDirectories(configPath.getParent());

            BasicConfigurationNode rootNode = loader.load();

            if (!Files.exists(configPath)) {
                config = configClass.getDeclaredConstructor().newInstance();
                save();
                return;
            }

            config = rootNode.get(configClass);
        } catch (Exception e) {
            Core.logger().error("Exception generated when loading config {}: {}", config, e.getMessage());
            try {
                config = configClass.getDeclaredConstructor().newInstance();
            } catch (Exception ignored) {}
        }
    }

    public void save() {
        try {
            BasicConfigurationNode rootNode = loader.createNode();
            rootNode.set(configClass, config);
            loader.save(rootNode);
        } catch (IOException e) {
            Core.logger().error("Exception generated when saving config {}: {}", config, e.getMessage());
        }
    }
}
