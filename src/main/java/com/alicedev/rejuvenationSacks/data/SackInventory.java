package com.alicedev.rejuvenationSacks.data;

import com.alicedev.rejuvenationSacks.RejuvenationSacks;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class SackInventory implements InventoryHolder {

    private Inventory inventory;
    private final RejuvenationSacks plugin;

    public SackInventory(RejuvenationSacks plugin, Integer rows) {
        this.inventory = plugin.getServer().createInventory(this, rows * 9);
        this.plugin = plugin;
    }

    @Override
    public Inventory getInventory(){
        return this.inventory;
    }
}
