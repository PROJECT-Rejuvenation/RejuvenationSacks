package com.alicedev.rejuvenationSacks.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MiniMessageUtils {
    public static Component mmFormat(String serial){
        return MiniMessage.miniMessage().deserialize(serial).decoration(TextDecoration.ITALIC,false);
    }
}
