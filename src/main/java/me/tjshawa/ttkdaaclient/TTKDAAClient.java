package me.tjshawa.ttkdaaclient;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import me.tjshawa.ttkdaaclient.command.CommandHandler;
import me.tjshawa.ttkdaaclient.listener.BukkitListener;
import me.tjshawa.ttkdaaclient.listener.PacketEventsListener;
import me.tjshawa.ttkdaaclient.manager.ConfigManager;
import me.tjshawa.ttkdaaclient.utils.LoggingUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

public final class TTKDAAClient extends JavaPlugin {

    public static TTKDAAClient INSTANCE;
    public static ConfigManager configManager;

    public static String prefix = "&b&lTTKDAA&f ";
    // 检查线程池
    private final ExecutorService packetExecutor = Executors.newSingleThreadExecutor();
    @Override
    public void onLoad() {
        INSTANCE = this;

        LoggingUtil.logInfo("&c&lTTKDAA LOADING");

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        // 注册Bukkit事件和Pe事件
    }
    @Override
    public void onEnable() {
        LoggingUtil.logInfo("&c&lTTKDAA ENABLING");
        LoggingUtil.logInfo("&b&lHello tjshawa!!!");
        configManager = new ConfigManager(this);
        configManager.initialize();
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketEventsListener(), PacketListenerPriority.MONITOR);
        Bukkit.getPluginManager().registerEvents(new BukkitListener(), this);
        // 注册命令
        getCommand("ttkdaa").setExecutor(new CommandHandler());
    }

    @Override
    public void onDisable() {
        LoggingUtil.logInfo("&c&lTTKDAA DISABLING");
        PacketEvents.getAPI().terminate();
    }
}
