package com.alicedev.rejuvenationSacks.events;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import com.alicedev.rejuvenationSacks.data.SackDataManager;
import com.alicedev.rejuvenationSacks.data.SackInventory;
import com.alicedev.rejuvenationSacks.data.SackTemplate;
import com.alicedev.rejuvenationSacks.utils.compatibility.MMOItemsCompat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

    //TODO: Separate listener for type masks into multiple handlers (mmotype,mythic,vanilla)
    //TODO: disable mask checking if the template's mask section is null
    //TODO: add a config option to invert a mask
    //TODO: Add UUID to sack item on first open, instead of on creation

    /**
     * SackEventListener:
     * constructor, used to initialise plugin startup data.
     *
     * @param plugin instance of main plugin class.
     */
    public SackEventListener(RejuvenationSacks plugin) {
        this.plugin = plugin;
        this.sackData = plugin.getSackDataManager();
        sackKey = new NamespacedKey(plugin, "sack_id");
        templateKey = new NamespacedKey(plugin, "sack_template");
    }

    /*public void onRightClick(PlayerInteractEvent event) throws Exception {
        if (event.getItem() == null || event.getAction().isLeftClick()) return;
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(templateKey, PersistentDataType.STRING)) return;

        //TODO: generate UUID on first use (give item without UUID, if right clicked and no UUID is found, generate one and open)

        event.setCancelled(true);

        UUID uuid = null;
        if (container.has(sackKey, PersistentDataType.STRING)) uuid = UUID.fromString(container.get(sackKey, PersistentDataType.STRING));
        else uuid = UUID.randomUUID();
        container.set(sackKey, PersistentDataType.STRING, uuid.toString());

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
    }*/
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) throws Exception {
        if (event.getItem() == null || event.getAction().isLeftClick()) return;

        // we dont care unless item is a sack
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!SackTemplate.itemIsSack(plugin,item)) return;

        // cancel in case it's a block
        event.setCancelled(true);

        // UUID the item if it has none (dummy item)
        if (!SackTemplate.sackHasUUID(plugin,item)) SackTemplate.uuidSack(plugin,item);

        // get the rest of item data
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String template_id = pdc.get(templateKey,PersistentDataType.STRING);
        UUID uuid = UUID.fromString(pdc.get(sackKey,PersistentDataType.STRING));

        // prepare inventory
        SackTemplate template = sackData.loadFromConfig(template_id);
        SackInventory invHolder = new SackInventory(template,uuid);
        Inventory inv = invHolder.getInventory();

        // loading contents from memory or from disk
        if (plugin.sackContents.containsKey(uuid)) inv.setContents(plugin.sackContents.get(uuid));
        else inv.setContents(sackData.loadInventory(uuid));

        // opening inventory
        player.openInventory(inv);
        plugin.openInventories.add(invHolder);
        invHolder.populate_blockers();
    }

    /**
     * onInventoryClose:
     * inventory close event handler
     * <p>saves sack contents to file and memory when closing a sack
     *
     * @param e InventoryCloseEvent
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
     * onInventoryInteract:
     * inventory click event handler
     * <p>checks for and cancels events whilst a sack inventory is open where:
     * <p>- Sack is clicked
     * <p>- Blocked slot ({@link SackTemplate}) is clicked
     * <p>- Item that does not match mask is clicked (if masks are enabled)
     *
     * @param e InventoryClickEvent
     */
    //TODO: separate this event listener method into exclusive methods for each type of restriction (mmotype,vanilla,etc)
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) throws Exception {
        InventoryHolder topInv = e.getView().getTopInventory().getHolder();
        Inventory clickedInv = e.getClickedInventory();
        if (!(topInv instanceof SackInventory) || clickedInv == null) return;

        SackInventory sackInv = (SackInventory) topInv;
        SackTemplate template = sackInv.getTemplate();
        List<String> mask = template.getMask();

        ItemStack target = clickedInv.getItem(e.getSlot());
        // Captures meta here in case of inverse hot-swapping of the blocker item.
        ItemMeta target_meta = target != null ? target.getItemMeta() : new ItemStack(Material.STONE).getItemMeta();
        if(e.getClick().isKeyboardClick()) target = e.getView().getBottomInventory().getItem(e.getHotbarButton());

        if(target == null) return;

        // cancel if any of these match
        boolean is_blocker = target_meta.isHideTooltip();
        boolean is_sack = SackTemplate.itemIsSack(plugin,target);
        boolean matches_mask = false;
        if (MMOItemsCompat.isMMOItem(target))
            matches_mask = mask.contains(MMOItemsCompat.getMMOItemType(target).toUpperCase());
        if(mask.isEmpty()) matches_mask = true;

        e.setCancelled(is_sack || is_blocker || !(matches_mask));
    }
}
