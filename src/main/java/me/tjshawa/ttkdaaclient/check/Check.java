package me.tjshawa.ttkdaaclient.check;

import me.tjshawa.ttkdaaclient.AlertUtil;
import me.tjshawa.ttkdaaclient.types.PlayerData;
import org.bukkit.ChatColor;

public abstract class Check {
    public String name;
    public PlayerData data;

    public double buffer;

    public Check(PlayerData data) {
        this.data = data;
    }

    public abstract void run(Object event);

    public void flag(String complex, String message) {
        data.violations++;
        AlertUtil.alert(complex, message, this);
    }

    public double reduceBuffer(double a) {
        buffer = Math.max(0, buffer - a);
        return buffer;
    }

    public double addBuffer() {
        buffer++;
        return buffer;
    }

    public double addBuffer2(double a) {
        buffer += a;
        return buffer;
    }

    public void debug(String ms) {
        if (data.debug) {
            data.player.sendMessage(ChatColor.translateAlternateColorCodes('&', ms));
        }
    }
}
