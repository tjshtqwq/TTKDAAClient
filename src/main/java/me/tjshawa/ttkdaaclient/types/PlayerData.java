package me.tjshawa.ttkdaaclient.types;

import me.tjshawa.ttkdaaclient.PlayerDataManager;
import me.tjshawa.ttkdaaclient.check.Check;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public Player player;
    public PlayerData(Player pp) {
        this.player = pp;
    }
    public double violations = 0;
    public List<Check> checks = new ArrayList<>();
    public boolean alert = false;
    public boolean debug = false;

    public int useEntityTicks = 100;
    public int joinTicks = 0;

    public double yaw  = 0;
    public double pitch = 0;
    public double lastYaw = 0;
    public double lastPitch = 0;
    public double deltaYaw = 0;
    public double deltaPitch = 0;
    public double lastDeltaYaw = 0;
    public double lastDeltaPitch = 0;
    public double joltYaw = 0;
    public double joltPitch = 0;

    public double x = 0;
    public double y = 0;
    public double z = 0;
    public double lastX = 0;
    public double lastY = 0;
    public double lastZ = 0;
    public double deltaX = 0;
    public double deltaY = 0;
    public double deltaZ = 0;
    public double lastDeltaX = 0;
    public double lastDeltaY = 0;
    public double lastDeltaZ = 0;
    public double joltX = 0;
    public double joltY = 0;
    public double joltZ = 0;

    public double deltaXZ = 0;

    public boolean ground = false;
    public boolean lastGround = false;
    public boolean lastLastGround = false;

    public int target;
}
