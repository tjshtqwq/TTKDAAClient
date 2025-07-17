package me.tjshawa.ttkdaaclient.utils;

import org.bukkit.Material;

public class BlockUtil {
    public static boolean isOre(Material material) {
        return material.toString().contains("ORE") || material.toString().contains("ANCIENT_DEBRIS");
    }

    public static boolean isRareOre(Material material) {
        return material.toString().contains("ORE") &&
                (material.toString().contains("DIAMOND"));
    }
}
