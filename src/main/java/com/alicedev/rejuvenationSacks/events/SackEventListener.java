package com.alicedev.rejuvenationSacks.events;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import com.alicedev.rejuvenationSacks.data.PluginConfigManager;
import com.alicedev.rejuvenationSacks.data.SackDataManager;
import com.alicedev.rejuvenationSacks.data.SackInventory;
import com.alicedev.rejuvenationSacks.data.SackTemplate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SackEventListener implements Listener {
    private final Map<UUID, ItemStack[]> sackContents = new HashMap<>();
    private RejuvenationSacks plugin;
    private PluginConfigManager pluginConfig;
    private SackDataManager sackData;
    private NamespacedKey sackKey;


    //TODO: Listener for blocked slot transfers
    //TODO: Listener for bags-within-bags
    //TODO: Listener for type masks (and a config option to abstain from them)

    /**
     * SackEventListener:
     * constructor, used to initialise plugin startup data.
     *
     * @param plugin - instance of main plugin class.
     */
    public SackEventListener(RejuvenationSacks plugin) {
        this.plugin = plugin;
        this.pluginConfig = plugin.getPluginConfigManager();
        this.sackData = plugin.getSackDataManager();
        sackKey = new NamespacedKey(plugin, "sack_id");
    }



    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(sackKey, PersistentDataType.STRING)) return;

        event.setCancelled(true);

        UUID uuid = UUID.fromString(container.get(sackKey, PersistentDataType.STRING));
        Player player = event.getPlayer();
        SackInventory invHolder = new SackInventory(plugin,1);
        Inventory inv = invHolder.getInventory();

        if (sackContents.containsKey(uuid)) {
            inv.setContents(sackContents.get(uuid));
        } else {
            inv.setContents(sackData.loadInventory(uuid));
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder(false) instanceof SackInventory)) return;

        Player player = (Player) event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(sackKey, PersistentDataType.STRING)) return;

        UUID uuid = UUID.fromString(container.get(sackKey, PersistentDataType.STRING));
        ItemStack[] contents = event.getInventory().getContents();
        sackContents.put(uuid, contents);
        sackData.saveInventory(uuid, contents);
    }
}
