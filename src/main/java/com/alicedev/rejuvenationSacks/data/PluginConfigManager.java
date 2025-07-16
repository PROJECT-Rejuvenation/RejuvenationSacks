package com.alicedev.rejuvenationSacks.data;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PluginConfigManager {
    private RejuvenationSacks plugin;
    private File templatesConfigFile;
    private FileConfiguration templatesConfig;

    /**
     * PluginConfigManager:
     * handles loading of yml config data.
     *
     * @param plugin main plugin instance.
     */
    public PluginConfigManager(RejuvenationSacks plugin) {
        this.plugin = plugin;
        this.templatesConfigFile = new File(plugin.getDataFolder(), "templates.yml");
        saveDefaultConfigs();
        reloadConfig();
    }

    /**
     * getTemplatesConfig:
     * Retrieves the player configuration.
     *
     * @return Player configuration
     */
    public FileConfiguration getTemplatesConfig() {
        return this.templatesConfig;
    }

    /**
     * saveTemplatesConfig:
     * Saves the player configuration to disk.
     */
    public void saveTemplatesConfig() {
        saveConfig(templatesConfig, templatesConfigFile, "PLAYER CONFIG SAVE FAILED!");
    }

    /**
     * reloadConfig:
     * Reloads configuration files.
     */
    public void reloadConfig() {
        plugin.logInfo("Loading Configs");
        this.templatesConfig = YamlConfiguration.loadConfiguration(templatesConfigFile);
        loadDefaultConfig("templates.yml", templatesConfig);
        plugin.logInfo("Configs Loaded");
    }

    /**
     * saveDefaultConfigs:
     * Saves default configurations from resources if they don't exist.
     */
    private void saveDefaultConfigs() {
        saveResource("templates.yml", templatesConfigFile);
    }

    /**
     * saveResource:
     * Saves a default configuration file from the plugin resources.
     *
     * @param resourcePath Path to the resource in the plugin JAR
     * @param file         File to save the resource to
     * @return void
     */
    private void saveResource(String resourcePath, File file) {
        if (!file.exists()) {
            plugin.saveResource(resourcePath, false);
        }
    }

    /**
     * loadDefaultConfig:
     * Loads default configuration from resources if not already existing.
     *
     * @param fileName   Name of the file to load
     * @param config     Configuration to load into
     * @return void
     */
    private void loadDefaultConfig(String fileName, FileConfiguration config) {
        InputStream inputStream = plugin.getResource(fileName);
        if (inputStream != null) {
            YamlConfiguration defaultConfig =
                    YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            config.setDefaults(defaultConfig);
        }
    }

    /**
     * Saves a configuration file.
     *
     * @param config     Configuration to save
     * @param configFile File to save the configuration to
     * @param errorMessage Error message to log in case of failure
     * @return void
     */
    private void saveConfig(FileConfiguration config, File configFile, String errorMessage) {
        if (config != null && configFile != null) {
            try {
                config.save(configFile);
            } catch (IOException e) {
                plugin.getLogger().severe(errorMessage);
                e.printStackTrace();
            }
        }
    }

    public String[] getTemplateIDs() {
        return getTemplatesConfig().getKeys(false).toArray(new String[0]);
    }
}
