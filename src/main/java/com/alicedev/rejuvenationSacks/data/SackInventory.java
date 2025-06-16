package com.alicedev.rejuvenationSacks.data;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class SackInventory implements InventoryHolder {

    private Inventory inventory;
    private final UUID uuid;
    private final SackTemplate template;

    public SackInventory(SackTemplate template, UUID uuid) {
        this.inventory = Bukkit.createInventory(this, template.getRows() * 9);
        this.template = template;
        this.uuid = uuid;
    }

    public SackTemplate getTemplate() {
        return template;
    }

    @Override
    public Inventory getInventory(){
        return this.inventory;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void populate_blockers() {
        Integer[] blacklist = template.getBlocked_slots();
        ItemStack blocker = new ItemStack(Material.BLACK_STAINED_GLASS_PANE,1);
        ItemMeta meta = blocker.getItemMeta();
        meta.setDisplayName(" ");
        meta.setHideTooltip(true);
        blocker.setItemMeta(meta);

        for(int slot : blacklist) {
            inventory.setItem(slot,blocker);
        }
    }
}
