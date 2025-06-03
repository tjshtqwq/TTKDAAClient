package me.tjshawa.ttkdaaclient.check;

import me.tjshawa.ttkdaaclient.utils.AlertUtil;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
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
        this.buffer = Math.max(0, buffer - a);
        return this.buffer;
    }

    public double addBuffer() {
        this.buffer ++;
        return buffer;
    }

    public double addBuffer2(double a) {
        this.buffer += a;
        return buffer;
    }

    public void debug(String ms) {
        if (this.data.debug) {
            this.data.player.sendMessage(ChatColor.translateAlternateColorCodes('&', ms));
        }
    }
}
