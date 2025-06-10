package com.alicedev.rejuvenationSacks;

import com.alicedev.rejuvenationSacks.commands.ReloadPluginCommand;
import com.alicedev.rejuvenationSacks.data.PluginConfigManager;
import com.alicedev.rejuvenationSacks.data.SackDataManager;
import com.alicedev.rejuvenationSacks.events.SackEventListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class RejuvenationSacks extends JavaPlugin {

    private static final Logger LOGGER = Logger.getLogger("RejuvenationSacks");
    private PluginConfigManager configManager;
    private SackDataManager sackDataManager;
    private RejuvenationSacks plugin = this;
    Boolean initialized = true;


    //TODO: onDisable function to close safely
    //TODO: Command to give a player a sack


    /**
     * onEnable:
     * runs on plugin enable.
     * initializes components.
     */
    @Override
    public void onEnable() {
        loadPlugin();
        logInfo("RejuvenationSacks enabled.");
    }

    /**
     * loadSackData:
     * loads sack data manager.
     */
    private void loadSackData() {
        sackDataManager = new SackDataManager(this);
    }

    /**
     * loadPluginConfigs: 
     * loads config manager.
     */
    private void loadPluginConfigs() {
        configManager = new PluginConfigManager(this);
    }

    /**
     * registerCommands:
     * registers plugin command executors & completion.
     */
    private void registerCommands() {
        getCommand("prsacks").setExecutor(new ReloadPluginCommand(plugin));
    }

    /**
     * registerEvents:
     * registers plugin event listeners.
     */
    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new SackEventListener(plugin),this);
    }

    public void loadPlugin() {
        logInfo(initialized ? "Reloading RejuvenationSacks..." : "Loading RejuvenationSacks..");
        initialized = true;

        loadPluginConfigs();
        loadSackData();

        HandlerList.unregisterAll(plugin);
        registerEvents();
        registerCommands();

        if(!getDataFolder().exists()) getDataFolder().mkdir();
    }

    /**
     * getSackDataManager:
     * getter for main sack Data manager.
     */
    public SackDataManager getSackDataManager() {
        return sackDataManager;
    }

    /**
     * getPluginConfigManager:
     * getter for main plugin Config manager.
     */
    public PluginConfigManager getPluginConfigManager() {
        return configManager;
    }

    /**
     * logInfo:
     * console log message, at info level.
     *
     * @param message message string
     */
    public void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    /**
     * logWarning:
     * console log message, at warning level.
     *
     * @param message message string
     */
    public void logWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }
}
