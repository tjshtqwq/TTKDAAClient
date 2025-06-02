package me.tjshawa.ttkdaaclient;

import me.tjshawa.ttkdaaclient.check.Check;
import me.tjshawa.ttkdaaclient.types.PlayerData;
import org.bukkit.Bukkit;

public class PunishUtil {
    public static void punish(PlayerData data, String complex, String message, Check check) {
        String alert = TTKDAAClient.configManager.getString("punish", "kick %player% %prefix% Cheating &c(%check%, VL: %vl%)");
        alert = alert.replaceAll("%player%", check.data.player.getName());
        alert = alert.replaceAll("%vl%", String.valueOf(check.data.violations));
        alert = alert.replaceAll("%maxvl%", String.valueOf(TTKDAAClient.configManager.getDouble("maxvl", 24d)));
        alert = alert.replaceAll("%check%", check.name);
        alert = alert.replaceAll("%prefix%", TTKDAAClient.prefix);
        alert = alert.replaceAll("%complex%", complex);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), alert);
    }
}
