package me.tjshawa.ttkdaaclient.utils;

import me.tjshawa.ttkdaaclient.TTKDAAClient;
import me.tjshawa.ttkdaaclient.check.Check;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
import org.bukkit.Bukkit;

public class PunishUtil {
    public static void punish(PlayerData data, String complex, String message, Check check) {
        String alert = TTKDAAClient.configManager.getString("punish", "kick %player% %prefix% Cheating &c(%check%, VL: %vl%)");
        alert = AlertUtil.getString(complex, check, alert);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), alert);
    }
}
