package me.tjshawa.ttkdaaclient.listener;

import me.tjshawa.ttkdaaclient.CheckManager;
import me.tjshawa.ttkdaaclient.LoggingUtil;
import me.tjshawa.ttkdaaclient.PlayerDataManager;
import me.tjshawa.ttkdaaclient.TTKDAAClient;
import me.tjshawa.ttkdaaclient.types.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerDataManager.addPlayerData(event.getPlayer().getUniqueId(), event.getPlayer());
        PlayerData data = PlayerDataManager.getPlayerData(event.getPlayer().getUniqueId());
        LoggingUtil.logInfo("Added player " + event.getPlayer().getName() + " to the player data manager.");
        data.checks = CheckManager.loadChecks(data);
        if (event.getPlayer().hasPermission("tatako.ttkdaa.alerts")) {
            data.alert = true;
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerJoinEvent event) {
        PlayerDataManager.removePlayerData(event.getPlayer().getUniqueId());
        LoggingUtil.logInfo("Removed player " + event.getPlayer().getName() + " from the player data manager.");
    }
}
