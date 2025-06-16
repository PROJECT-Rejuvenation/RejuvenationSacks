package com.alicedev.rejuvenationSacks.data;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class SackTemplate {
    private String identifier;
    private int rows;
    private Material material;
    private Component display_name; // Uses MiniMessage.miniMessage().deserialize("<#225511>Colors :D");
    private List<Component> display_lore;
    private Integer[] blocked_slots;
    private String mask;

    /**
     * SackTemplate:
     * parses config templates
     */
    public SackTemplate(String identifier) {
        this.identifier = identifier;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getRows() {
        return rows;
    }

    public void setDisplay_name(Component display_name) {
        this.display_name = display_name;
    }

    public void setDisplay_lore(List<Component> display_lore) {
        this.display_lore = display_lore;
    }

    public void setBlocked_slots(Integer[] blocked_slots) {
        this.blocked_slots = blocked_slots;
    }

    public Integer[] getBlocked_slots() {
        return blocked_slots;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Build template into an ItemStack
     */
    public static ItemStack createSack(JavaPlugin plugin, SackTemplate template) {
        // initialize and get bindings to new ItemStack
        ItemStack sack = new ItemStack(template.material);
        ItemMeta meta = sack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        // set sack instance UUID
        NamespacedKey uuid_key = new NamespacedKey(plugin, "sack_id");
        UUID uuid = UUID.randomUUID();
        pdc.set(uuid_key, PersistentDataType.STRING, uuid.toString());

        // set sack template reference
        NamespacedKey template_key = new NamespacedKey(plugin, "sack_template");
        pdc.set(template_key, PersistentDataType.STRING, template.identifier);

        // set display metas
        meta.itemName(template.display_name);
        meta.lore(template.display_lore);

        // apply meta
        sack.setItemMeta(meta);
        return sack;
    }

    public static boolean itemIsSack(JavaPlugin plugin, ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        NamespacedKey template_key = new NamespacedKey(plugin, "sack_template");
        return pdc.has(template_key, PersistentDataType.STRING);
    }
}
