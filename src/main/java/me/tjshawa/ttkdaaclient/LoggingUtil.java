package me.tjshawa.ttkdaaclient;

import org.bukkit.ChatColor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LoggingUtil {
    public static void logInfo(String message) {
        TTKDAAClient.INSTANCE.getLogger().info(ChatColor.translateAlternateColorCodes('&', message));
    }
    public static void logWarning(String message) {
        TTKDAAClient.INSTANCE.getLogger().warning(ChatColor.translateAlternateColorCodes('&', message));
    }
    public static void logSevere(String message) {
        TTKDAAClient.INSTANCE.getLogger().severe(ChatColor.translateAlternateColorCodes('&', message));
    }

    // 写出一些内容到一个指定的插件文件夹下的指定文件
    public static void writeToFile(String content, String fileName) {
        // TTKDAAClient.INSTANCE获取插件目录
        try (FileWriter fw = new FileWriter(new File(TTKDAAClient.INSTANCE.getDataFolder(), fileName), true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            File file = new File(TTKDAAClient.INSTANCE.getDataFolder(), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            bw.write(content);
            bw.newLine();

        } catch (IOException e) {
            logSevere("&c&l写出失败：\n" + e.getMessage());
        }
    }
}
