package com.alicedev.rejuvenationSacks.data;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SackTemplate {
    private final String identifier;
    private int rows;
    private Material material;
    private String head_texture;
    private Component display_name; // Uses MiniMessage.miniMessage().deserialize("<#225511>Colors :D");
    private List<Component> display_lore;
    private Integer[] blocked_slots;
    private List<String> mask;

    /**
     * SackTemplate:
     * holder for the template defined in config.
     * <p>constructed in {@link SackDataManager}
     *
     * @see SackDataManager#loadFromConfig(String)
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

    public void setMask(List<String> mask) {
        this.mask = mask;
    }

    public List<String> getMask() {
        return mask;
    }

    public void setHeadTexture(String head_texture) {
        this.head_texture = head_texture;
    }

    public String getHeadTexture() {
        return head_texture;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Build template into an ItemStack
     */
    public static ItemStack createSack(JavaPlugin plugin, SackTemplate template) throws Exception {
        // initialize and get bindings to new ItemStack
        ItemStack sack = new ItemStack(template.material);
        ItemMeta meta = sack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        // set head texture if any
        if (meta instanceof SkullMeta && template.head_texture != null) {
            String mojang_tex = SackDataManager.extractTextureURL(new String(Base64.getDecoder().decode(template.head_texture)));
            URL url = URL.of(new URI(mojang_tex), null);

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();

            textures.setSkin(url);

            profile.setTextures(textures);
            ((SkullMeta) meta).setPlayerProfile(profile);
        }

        // set sack instance UUID
        //NamespacedKey uuid_key = new NamespacedKey(plugin, "sack_id");
        //UUID uuid = UUID.randomUUID();
        //pdc.set(uuid_key, PersistentDataType.STRING, uuid.toString());

        // set sack template reference
        NamespacedKey template_key = new NamespacedKey(plugin, "sack_template");
        pdc.set(template_key, PersistentDataType.STRING, template.identifier);

        // set display metas
        meta.itemName(template.display_name);
        meta.lore(template.display_lore);

        // set max stack size to 1 to avoid conflicts when UUID'ing
        meta.setMaxStackSize(1);

        // apply meta
        sack.setItemMeta(meta);
        return sack;
    }

    /**
     * uuidSack:
     * generates and applies a UUID to the given
     * ItemStack, if it is a sack.
     *
     * @return the UUID'd ItemStack
     */
    public static ItemStack uuidSack(JavaPlugin plugin, ItemStack itemStack) throws Exception {
        if (!itemIsSack(plugin,itemStack)) throw new Exception("ItemStack to UUID is not a valid sack!");

        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        NamespacedKey uuid_key = new NamespacedKey(plugin, "sack_id");
        UUID uuid = UUID.randomUUID();
        pdc.set(uuid_key, PersistentDataType.STRING, uuid.toString());

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static boolean itemIsSack(JavaPlugin plugin, ItemStack itemStack) {
        if (itemStack == null) return false;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        NamespacedKey template_key = new NamespacedKey(plugin, "sack_template");
        return pdc.has(template_key, PersistentDataType.STRING);
    }

    public static boolean sackHasUUID(JavaPlugin plugin, ItemStack itemStack) {
        if (!itemIsSack(plugin, itemStack)) return false;
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        NamespacedKey uuid_key = new NamespacedKey(plugin, "sack_id");
        return pdc.has(uuid_key, PersistentDataType.STRING);
    }
}
