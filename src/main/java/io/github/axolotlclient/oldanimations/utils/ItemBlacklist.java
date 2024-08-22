package io.github.axolotlclient.oldanimations.utils;

import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;

import java.util.HashMap;
import java.util.Map;

public class ItemBlacklist {

    // map to store blacklisted items
    // some items are not quite compatible with 1.7's item position
    private static final Map<Class<?>, Boolean> blacklistedItems = new HashMap<Class<?>, Boolean>() {{
        put(SkullItem.class, true);
        put(BannerItem.class, true);
    }};

    // method to check if an item is blacklisted
    public static boolean isPresent(ItemStack stack) {
        return blacklistedItems.containsKey(stack.getItem().getClass());
    }
}
