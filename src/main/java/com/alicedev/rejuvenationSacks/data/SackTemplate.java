package com.alicedev.rejuvenationSacks.data;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    public void setDisplay_name(Component display_name) {
        this.display_name = display_name;
    }

    public void setDisplay_lore(List<Component> display_lore) {
        this.display_lore = display_lore;
    }

    public void setBlocked_slots(Integer[] blocked_slots) {
        this.blocked_slots = blocked_slots;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public static ItemStack createSack(JavaPlugin plugin, SackTemplate template) {
        ItemStack sack = new ItemStack(template.material);
        ItemMeta meta = sack.getItemMeta();
        UUID uuid = UUID.randomUUID();
        NamespacedKey key = new NamespacedKey(plugin, "sack_id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, uuid.toString());
        meta.itemName(template.display_name);
        meta.lore(template.display_lore);
        sack.setItemMeta(meta);
        return sack;
    }
}
