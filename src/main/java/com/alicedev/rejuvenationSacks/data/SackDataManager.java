package com.alicedev.rejuvenationSacks.data;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SackDataManager {
    private RejuvenationSacks plugin;
    private PluginConfigManager configManager;

    public SackDataManager(RejuvenationSacks plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getPluginConfigManager();
    }

    public SackTemplate loadFromConfig(String id) throws Exception {
        //plugin.logInfo("Loading sack template: " + id);

        ConfigurationSection section = configManager.getTemplatesConfig().getConfigurationSection(id);
        if (section == null) {
            plugin.logWarning("Section '" + id + "' not found in configuration.");
            throw new Exception("Section '" + id + "' not found in configuration.");
        }
        SackTemplate template = new SackTemplate(id);
        template.setRows(section.getInt("rows"));
        template.setMaterial(Material.valueOf(section.getString("material")));
        template.setDisplay_name(MiniMessage.miniMessage().deserialize(section.getString("name")).decoration(TextDecoration.ITALIC,false));
        List<Component> lorelines = new ArrayList<>();
        for (String s : (List<String>) section.getList("lore")){
            lorelines.add(MiniMessage.miniMessage().deserialize(s).decoration(TextDecoration.ITALIC,false));
        }
        template.setDisplay_lore(lorelines);
        List<Integer> blots = (List<Integer>) section.getList("blocked-slots");
        template.setBlocked_slots(blots.toArray(new Integer[0]));
        template.setMask(section.getConfigurationSection("mask").getStringList("mmotype"));

        return template;
    }

    public void saveInventory(UUID uuid, ItemStack[] contents) {
        File file = new File(plugin.getInstances_dir(), uuid + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        for (int i = 0; i < contents.length; i++) {
            config.set("slot." + i, contents[i]);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.logWarning("Failed to save inventory for UUID: " + uuid);
            e.printStackTrace();
        }
    }

    public ItemStack[] loadInventory(UUID uuid) {
        File file = new File(plugin.getInstances_dir(), uuid + ".yml");
        if (!file.exists()) return new ItemStack[9];

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ItemStack[] contents = new ItemStack[9];

        for (int i = 0; i < contents.length; i++) {
            contents[i] = config.getItemStack("slot." + i);
        }

        return contents;
    }

}
