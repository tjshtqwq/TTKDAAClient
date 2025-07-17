package me.tjshawa.ttkdaaclient.manager;

import me.tjshawa.ttkdaaclient.check.impl.AutoBotBaritone;
import me.tjshawa.ttkdaaclient.check.impl.ClusterXray;
import me.tjshawa.ttkdaaclient.utils.LoggingUtil;
import me.tjshawa.ttkdaaclient.check.Check;
import me.tjshawa.ttkdaaclient.check.GenericPacketHandle;
import me.tjshawa.ttkdaaclient.check.impl.MachineLearningAimBot;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {
    // 列表所有检测
    public static List<Class> checks = new ArrayList<>();
    static {
        // ### BASE CHECKS ###
        checks.add(GenericPacketHandle.class);
        // ### AIMBOT CHECKS ###
        checks.add(MachineLearningAimBot.class);
        checks.add(AutoBotBaritone.class);
        // ### XRAY CHECKS ###
        checks.add(ClusterXray.class);
    }
    public static List<Check> loadChecks(PlayerData data) {
        List<Check> rc = new ArrayList<>();
        for (Class check : checks) {
            try {
                rc.add((Check) check.getConstructor(PlayerData.class).newInstance(data));
            } catch (Exception e) {
                LoggingUtil.logSevere("Failed to load check " + check.getName());
            }
        }
        return rc;
    }
}
