package me.tjshawa.ttkdaaclient.utils;

import me.tjshawa.ttkdaaclient.TTKDAAClient;
import me.tjshawa.ttkdaaclient.check.Check;
import me.tjshawa.ttkdaaclient.event.TTKDAAFlagEvent;
import me.tjshawa.ttkdaaclient.manager.PlayerDataManager;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AlertUtil {
    public static void alert(String complex, String message, Check check) {
        String alert = TTKDAAClient.configManager.getString("alert-message", "%prefix% &b%player% &cis using %check% (&7%complex%&c), VL: %vl%/%maxvl%");
        alert = alert.replaceAll("%player%", check.data.player.getName());
        alert = alert.replaceAll("%vl%", String.valueOf(check.data.violations));
        alert = alert.replaceAll("%maxvl%", String.valueOf(TTKDAAClient.configManager.getDouble("maxvl", 24d)));
        alert = alert.replaceAll("%check%", check.name);
        alert = alert.replaceAll("%prefix%", TTKDAAClient.prefix);
        alert = alert.replaceAll("%complex%", complex);
        alert = alert.replaceAll("%message%", message);
        for (Player pp : Bukkit.getOnlinePlayers()) {
            PlayerData data = PlayerDataManager.getPlayerData(pp.getUniqueId());
            if (data != null && data.alert) {
                pp.sendMessage(ChatColor.translateAlternateColorCodes('&', alert));
            }
        }
        if (check.data.violations >= TTKDAAClient.configManager.getDouble("maxvl", 24d)) {
            PunishUtil.punish(check.data, complex, message, check);
        }
        TTKDAAFlagEvent event = new TTKDAAFlagEvent(check.data.player, complex, message, check, check.data);
        Bukkit.getScheduler().runTaskAsynchronously(TTKDAAClient.INSTANCE,  () -> Bukkit.getPluginManager().callEvent(event));
    }
}
