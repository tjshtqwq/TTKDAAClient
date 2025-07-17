package me.tjshawa.ttkdaaclient.listener;

import me.tjshawa.ttkdaaclient.TTKDAAClient;
import me.tjshawa.ttkdaaclient.manager.CheckManager;
import me.tjshawa.ttkdaaclient.utils.LoggingUtil;
import me.tjshawa.ttkdaaclient.manager.PlayerDataManager;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerDataManager.removePlayerData(event.getPlayer().getUniqueId());
        LoggingUtil.logInfo("Removed player " + event.getPlayer().getName() + " from the player data manager.");
    }
    @EventHandler ()
    public void onBlockBreak(BlockBreakEvent event) {
        PlayerData data = PlayerDataManager.getPlayerData(event.getPlayer().getUniqueId());
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(event.getBlock(), event.getPlayer());
        if (data != null) {
            data.checks.forEach(check -> check.run(blockBreakEvent));
        }
    }

    @EventHandler ()
    public void onBlockPlace(BlockPlaceEvent event) {
        PlayerData data = PlayerDataManager.getPlayerData(event.getPlayer().getUniqueId());
        BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(event.getBlock(), event.getBlockReplacedState(), event.getBlockAgainst(), event.getItemInHand(), event.getPlayer(), event.canBuild(), event.getHand());
        if (data != null) {
            data.checks.forEach(check -> check.run(blockPlaceEvent));
        }
    }
}
