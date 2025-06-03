package me.tjshawa.ttkdaaclient.manager;

import me.tjshawa.ttkdaaclient.utils.LoggingUtil;
import me.tjshawa.ttkdaaclient.TTKDAAClient;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private final TTKDAAClient plugin;
    private YamlConfiguration config;
    private File configFile;

    public ConfigManager(TTKDAAClient plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        TTKDAAClient.prefix = getString("prefix", "&b&lTTKDAA&f ");
    }

    public void reload() {
        TTKDAAClient.INSTANCE.reloadConfig();
        config = YamlConfiguration.loadConfiguration(configFile);

        TTKDAAClient.prefix = getString("prefix", "&b&lTTKDAA&f ");
        // 重载所有玩家的检测
        PlayerDataManager.reloadAllChecks();

        LoggingUtil.logInfo("&a配置文件重载成功！");
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

     public double getDouble(String path, double defaultValue) {
        return config.getDouble(path, defaultValue);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            LoggingUtil.logSevere("&c&l保存配置失败：\n" + e.getMessage());
        }
    }
}
