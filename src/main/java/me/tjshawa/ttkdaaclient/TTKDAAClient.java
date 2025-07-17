package me.tjshawa.ttkdaaclient;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import lombok.Getter;
import lombok.SneakyThrows;
import me.tjshawa.ttkdaaclient.command.CommandHandler;
import me.tjshawa.ttkdaaclient.listener.BukkitListener;
import me.tjshawa.ttkdaaclient.listener.PacketEventsListener;
import me.tjshawa.ttkdaaclient.manager.CheckManager;
import me.tjshawa.ttkdaaclient.manager.ConfigManager;
import me.tjshawa.ttkdaaclient.ml.MLBackend;
import me.tjshawa.ttkdaaclient.ml.djl.DJLMLBackend;
import me.tjshawa.ttkdaaclient.ml.remote.RemoteMLBackend;
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
    public static final ExecutorService packetExecutor = Executors.newSingleThreadExecutor();

    @Getter
    private MLBackend mLBackend;

    @Override
    public void onLoad() {
        INSTANCE = this;

        LoggingUtil.logInfo("&c&lTTKDAA LOADING");

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        // 注册Bukkit事件和Pe事件
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        LoggingUtil.logInfo("&c&lTTKDAA ENABLING");
        LoggingUtil.logInfo("&b&lHello tjshawa!!!");
        configManager = new ConfigManager(this);
        configManager.initialize();

        String backendDescriptor = configManager.getString("ml-backend.type", "remote");
        if (backendDescriptor.equals("remote")) {
            String token = configManager.getString("ml-backend.server-url");
            String endpoint = configManager.getString("ml-backend.server-token");
            mLBackend = new RemoteMLBackend(endpoint, token);

            LoggingUtil.logInfo("&aUsing remote as ML backend.");
        } else if (backendDescriptor.equals("djl")) {
            //noinspection ExtractMethodRecommender
            Thread thread = new Thread(() -> mLBackend = new DJLMLBackend(
                    configManager.getString("ml-backend.model-path"),
                    configManager.getBoolean("ml-backend.compatible-mode", false)
            ));
            // DJL loads version.properties via Thread.currentThread().getContextClassLoader().getResources()
            // which defaults to the AppClassLoader (referring to Spigot JAR), not the plugin JAR where the file resides.
            // so, we override the context ClassLoader to ensure correct resource loading.
            // See: Platform#detectPlatform, fromSystem, ClassLoaderUtils#getResources
            thread.setContextClassLoader(getClass().getClassLoader());
            thread.start();
            thread.join();

            LoggingUtil.logInfo("&aUsing DJL as ML backend.");
        } else {
            getLogger().info("&c&lUnknown backend type: &e&l" + backendDescriptor);
        }

        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketEventsListener(), PacketListenerPriority.MONITOR);
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        // 注册命令
        getCommand("ttkdaa").setExecutor(new CommandHandler());
        LoggingUtil.logInfo("&cLoaded checks:");
        for (Class<?> check : CheckManager.checks) {
            LoggingUtil.logInfo("&b" + check.getSimpleName());
        }
    }

    @Override
    public void onDisable() {
        LoggingUtil.logInfo("&c&lTTKDAA DISABLING");
        PacketEvents.getAPI().terminate();
    }
}
