package com.alicedev.rejuvenationSacks.utils.compatibility;

import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.inventory.ItemStack;

public class MMOItemsCompat {

    public static Boolean isMMOItem(ItemStack target) {
        if (target == null) return false;
        NBTItem nbtItem = NBTItem.get(target);
        return nbtItem.hasType();
    }

    public static String getMMOItemType(ItemStack target) throws Exception {
        if (!isMMOItem(target)) throw new Exception("Target item is not a valid MMOItem!");

        NBTItem nbtItem = NBTItem.get(target);
        return nbtItem.getString("MMOITEMS_ITEM_TYPE");
    }
}
