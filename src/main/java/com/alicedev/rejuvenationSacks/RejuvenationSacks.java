package com.alicedev.rejuvenationSacks;

import com.alicedev.rejuvenationSacks.commands.GenerateSackCommand;
import com.alicedev.rejuvenationSacks.commands.ReloadPluginCommand;
import com.alicedev.rejuvenationSacks.data.PluginConfigManager;
import com.alicedev.rejuvenationSacks.data.SackDataManager;
import com.alicedev.rejuvenationSacks.data.SackInventory;
import com.alicedev.rejuvenationSacks.events.SackEventListener;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RejuvenationSacks extends JavaPlugin {

    private static final Logger LOGGER = Logger.getLogger("RejuvenationSacks");
    private PluginConfigManager configManager;
    private SackDataManager sackDataManager;
    private RejuvenationSacks plugin = this;
    private File instances_dir;
    public Map<UUID, ItemStack[]> sackContents = new HashMap<>();
    public Set<SackInventory> openInventories = new HashSet<>();
    Boolean initialized = true;



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
     * onDisable:
     * runs on plugin disable.
     * safely terminates components.
     */
    @Override
    public void onDisable() {
        safeCloseSacks();
        logInfo("RejuvenationSacks disabled.");
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
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        instances_dir = new File(getDataFolder().getAbsolutePath() + "/instances");
        if(!instances_dir.exists()) instances_dir.mkdir();

        configManager = new PluginConfigManager(this);
    }

    /**
     * registerCommands:
     * registers plugin command executors & completion.
     */
    private void registerCommands() {
        getCommand("prsacks").setExecutor(new ReloadPluginCommand(plugin));
        getCommand("gensack").setExecutor(new GenerateSackCommand(plugin));
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
     * safeCloseSacks
     * closes and saves all open sack inventories safely
     */
    public void safeCloseSacks() {
        openInventories.forEach(sackInv -> {
            sackInv.getInventory().close();
            UUID uuid = sackInv.getUuid();
            ItemStack[] contents = sackInv.getInventory().getContents();
            sackDataManager.saveInventory(uuid, contents);
        });
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
     * getInstances_dir:
     * getter for sack instances directory.
     */
    public File getInstances_dir() {
        return instances_dir;
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
