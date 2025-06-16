package com.alicedev.rejuvenationSacks.events;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import com.alicedev.rejuvenationSacks.data.PluginConfigManager;
import com.alicedev.rejuvenationSacks.data.SackDataManager;
import com.alicedev.rejuvenationSacks.data.SackInventory;
import com.alicedev.rejuvenationSacks.data.SackTemplate;
import com.alicedev.rejuvenationSacks.utils.MiniMessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class SackEventListener implements Listener {
    private RejuvenationSacks plugin;
    private SackDataManager sackData;
    private NamespacedKey sackKey;
    private NamespacedKey templateKey;

    //TODO: Listener for type masks (and a config option to abstain from them)
    //TODO: Add UUID to sack item on first open, instead of on creation

    /**
     * SackEventListener:
     * constructor, used to initialise plugin startup data.
     *
     * @param plugin - instance of main plugin class.
     */
    public SackEventListener(RejuvenationSacks plugin) {
        this.plugin = plugin;
        this.sackData = plugin.getSackDataManager();
        sackKey = new NamespacedKey(plugin, "sack_id");
        templateKey = new NamespacedKey(plugin, "sack_template");
    }



    @EventHandler
    public void onRightClick(PlayerInteractEvent event) throws Exception {
        if (event.getItem() == null || event.getAction().isLeftClick()) return;
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(sackKey, PersistentDataType.STRING)) return;

        event.setCancelled(true);

        UUID uuid = UUID.fromString(container.get(sackKey, PersistentDataType.STRING));
        String template_id = container.get(templateKey,PersistentDataType.STRING);

        Player player = event.getPlayer();
        SackTemplate template = sackData.loadFromConfig(template_id);
        SackInventory invHolder = new SackInventory(template,uuid);
        Inventory inv = invHolder.getInventory();


        if (plugin.sackContents.containsKey(uuid)) {
            inv.setContents(plugin.sackContents.get(uuid));
        } else {
            inv.setContents(sackData.loadInventory(uuid));
        }

        player.openInventory(inv);
        plugin.openInventories.add(invHolder);
        invHolder.populate_blockers();
    }

    /**
     * onInventoryClose
     * inventory close event handler
     * saves sack contents to file and memory when closing a sack
     *
     * @param e
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        InventoryHolder invHolder = e.getInventory().getHolder();
        if (!(invHolder instanceof SackInventory)) return;
        SackInventory sackInv = (SackInventory) invHolder;

        UUID uuid = sackInv.getUuid();
        ItemStack[] contents = sackInv.getInventory().getContents();

        plugin.sackContents.put(uuid, contents);
        plugin.openInventories.remove(sackInv);
        sackData.saveInventory(uuid, contents);
    }

    /**
     * onInventoryInteract
     * inventory click event handler
     * checks for and cancels events whilst a sack inventory is open where:
     * - Sack is clicked
     * - Blocked slot ({@link SackTemplate}) is clicked
     *
     * @param e InventoryClickEvent
     */
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e){
        InventoryHolder topInv = e.getView().getTopInventory().getHolder();
        if (!(topInv instanceof SackInventory)) return;

        ItemStack target = e.getClickedInventory().getItem(e.getSlot());
        // Captures meta here in case of inverse hot-swapping of the blocker item.
        ItemMeta target_meta = target != null ? target.getItemMeta() : new ItemStack(Material.STONE).getItemMeta();
        if(e.getClick().isKeyboardClick()) target = e.getView().getBottomInventory().getItem(e.getHotbarButton());

        boolean is_blocker = target_meta.isHideTooltip();
        boolean is_sack = SackTemplate.itemIsSack(plugin,target);

        e.setCancelled(is_sack || is_blocker);
    }
}
