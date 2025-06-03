package me.tjshawa.ttkdaaclient.check.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tjshawa.ttkdaaclient.utils.AimbotDetector;
import me.tjshawa.ttkdaaclient.TTKDAAClient;
import me.tjshawa.ttkdaaclient.check.Check;
import me.tjshawa.ttkdaaclient.utils.types.EvictingList;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MachineLearningAimBot extends Check {

    private final boolean collectDataEnabled;
    private final double bufferMax;
    private final double bufferReduceFactor;
    private final String serverToken;
    private final String serverUrl;

    public MachineLearningAimBot(PlayerData data) {
        super(data);
        this.collectDataEnabled = TTKDAAClient.configManager.getBoolean("checks.machine-learning-aimbot.collect-data", true);
        this.bufferMax = TTKDAAClient.configManager.getDouble("checks.machine-learning-aimbot.buffer", 1.5);
        this.bufferReduceFactor = TTKDAAClient.configManager.getDouble("checks.machine-learning-aimbot.buffer-reduce", 1.5);
        this.serverToken = TTKDAAClient.configManager.getString("checks.machine-learning-aimbot.server-token");
        this.serverUrl = TTKDAAClient.configManager.getString("checks.machine-learning-aimbot.server-url");
        this.name = "MachineLearningAimBot";
    }

    EvictingList<Double> pitchesAcc = new EvictingList<>(20);
    EvictingList<Double> yawsAcc = new EvictingList<>(20);
    EvictingList<Double> pitches = new EvictingList<>(20);
    EvictingList<Double> yaws = new EvictingList<>(20);
    EvictingList<Double> angles = new EvictingList<>(20);
    int lastTarget;

    @Override
    public void run(Object event) {
        if (!(event instanceof PacketReceiveEvent)) {
            return;
        }

        PacketReceiveEvent packetEvent = (PacketReceiveEvent) event;
        if (packetEvent.getPacketType() != PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION) {
            return;
        }

        if (data.joinTicks <= 20) {
            return;
        }

        if (data.useEntityTicks >= 5) {
            return;
        }

        if (lastTarget != data.target) {
            lastTarget = data.target;
            pitchesAcc.clear();
            yawsAcc.clear();
            angles.clear();
            pitches.clear();
            yaws.clear();
            return;
        }

        // 强行通过entityID获取entity
        Entity target = SpigotConversionUtil.getEntityById(data.player.getWorld(), data.target);
        // 获取转头数据
        if (target == null) return;
        final double deltaYaw = data.deltaYaw;
        final double deltaPitch = data.deltaPitch;
        final double accYaw = data.joltYaw;
        final double accPitch = data.joltPitch;
        final double deltaXZ = data.deltaXZ;
        Vector playerLookDir = data.player.getEyeLocation().getDirection();
        Vector playerEyeLoc = data.player.getEyeLocation().toVector();
        Vector entityLoc = target.getLocation().toVector();
        Vector playerEntityVec = entityLoc.subtract(playerEyeLoc);
        double angle = playerLookDir.angle(playerEntityVec);

        if ((deltaYaw > 0.0 || deltaPitch > 0.0) && deltaXZ > 0.0) {
            pitches.add(deltaPitch);
            yaws.add(deltaYaw);
            angles.add(angle);
            pitchesAcc.add(accPitch);
            yawsAcc.add(accYaw);
        }

        if (!pitches.isFull()) {
            return;
        }

        if (TTKDAAClient.configManager.getBoolean("checks.machine-learning-aimbot.collect-data", true)) {
            try {
                File dataDir = new File(TTKDAAClient.INSTANCE.getDataFolder(), "data");
                if (!dataDir.exists() && !dataDir.mkdirs()) {
                    throw new IOException("Failed to create data directory: " + dataDir.getAbsolutePath());
                }

                File csvFile = new File(dataDir, data.player.getName() + ".csv");
                try (FileWriter writer = new FileWriter(csvFile, true)) {
                    StringBuilder sb = new StringBuilder();

                    appendListToStringBuilder(sb, pitches);
                    appendListToStringBuilder(sb, pitchesAcc);
                    appendListToStringBuilder(sb, angles);
                    appendListToStringBuilder(sb, yawsAcc);

                    for (int i = 0; i < yaws.size(); i++) {
                        sb.append(yaws.get(i));
                        if (i != yaws.size() - 1) {
                            sb.append(",");
                        }
                    }

                    writer.write(sb.toString());
                    writer.write("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ArrayList<Double> combined = new ArrayList<>();
        combined.addAll(pitches);
        combined.addAll(pitchesAcc);
        combined.addAll(angles);
        combined.addAll(yawsAcc);
        combined.addAll(yaws);

        Bukkit.getScheduler().runTaskAsynchronously(TTKDAAClient.INSTANCE, () -> {
            try {
                AimbotDetector.DetectionResponse response = AimbotDetector.predict(combined, TTKDAAClient.configManager.getString("checks.machine-learning-aimbot.server-token"),
                        TTKDAAClient.configManager.getString("checks.machine-learning-aimbot.server-url"));
                if (response != null) {
                    debug(response.predicted + ", " + buffer);
                    if (response.predicted > 0.5) {
                        if (addBuffer2(response.predicted - 0.5) > TTKDAAClient.configManager.getDouble("checks.machine-learning-aimbot.buffer", 1.5)) {
                            flag("aim.ml.a", String.format("P=%.2f",  response.predicted * 100));
                            if (buffer > TTKDAAClient.configManager.getDouble("checks.machine-learning-aimbot.buffer", 1.5) + 0.5) {
                                buffer = TTKDAAClient.configManager.getDouble("checks.machine-learning-aimbot.buffer", 1.5) + 0.5;
                            }
                        }
                    } else {
                        reduceBuffer((0.5 - response.predicted) * TTKDAAClient.configManager.getDouble("checks.machine-learning-aimbot.buffer-reduce", 1.5));
                    }
                }
            } catch (Exception ignored) {
            }
        });
        pitches.clear();
        yaws.clear();
        angles.clear();
        pitchesAcc.clear();
        yawsAcc.clear();
    }

    private void appendListToStringBuilder(StringBuilder sb, EvictingList<Double> list) {
        for (Double value : list) {
            sb.append(value).append(",");
        }
    }
}

