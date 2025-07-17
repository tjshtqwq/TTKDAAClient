package me.tjshawa.ttkdaaclient.check.impl;

import me.tjshawa.ttkdaaclient.TTKDAAClient;
import me.tjshawa.ttkdaaclient.check.Check;
import me.tjshawa.ttkdaaclient.utils.types.EvictingList;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import java.util.*;

import static me.tjshawa.ttkdaaclient.utils.BlockUtil.isOre;
import static me.tjshawa.ttkdaaclient.utils.BlockUtil.isRareOre;

public class ClusterXray extends Check {
    private Set<Location> playerPlaced = new HashSet<>();
    private HashMap<String, EvictingList<Location>> pathW = new HashMap<>();
    private int minedVein = 0;
    private long lastClear = 0;
    private Map<String, Location> lastVeinLocation = new HashMap<>();


    private int totalTurns = 0;
    private int branchCount = 0;
    private int yChanges = 0;

    private int enviDetectionRange = 3;

    // 定义所有的阈值
    private int airThreshold = 14;
    private int caveAirMultiplier = 5;
    private int veinMaxDistance = 5;
    private int veinThreshold = 3;

    private int turnThreshold = 10;
    private int branchThreshold = 6;
    private int yChangeThreshold = 4;

    public ClusterXray(PlayerData data) {
        super(data);
        this.name = "ClusterXray";
        this.configName = "cluster-xray";

        this.enviDetectionRange = TTKDAAClient.configManager.getInt("checks.cluster-xray.envi-detection-range", 3);

        turnThreshold = TTKDAAClient.configManager.getInt("checks.cluster-xray.turn-threshold", 10);
        branchThreshold = TTKDAAClient.configManager.getInt("checks.cluster-xray.branch-threshold", 6);
        yChangeThreshold = TTKDAAClient.configManager.getInt("checks.cluster-xray.y-change-threshold", 4);

        airThreshold = TTKDAAClient.configManager.getInt("checks.cluster-xray.air-threshold", 14);
        caveAirMultiplier = TTKDAAClient.configManager.getInt("checks.cluster-xray.cave-air-multiplier", 5);

        veinMaxDistance = TTKDAAClient.configManager.getInt("checks.cluster-xray.max-cluster-distance", 5);
        veinThreshold = TTKDAAClient.configManager.getInt("checks.cluster-xray.cluster-threshold", 3);

    }

    @Override
    public void run(Object event) {
        if (!isEnabled()) return;
        if (now() - lastClear > 10 * 60 * 1000L) {
            lastClear = now();
            pathW.clear();
            minedVein = 0;
            lastVeinLocation.clear();
            totalTurns = 0;
            branchCount = 0;
            yChanges = 0;
        }
        if (event instanceof BlockBreakEvent) {
            BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
            Block block = blockBreakEvent.getBlock();
            if (isRareOre(block.getType()) && !playerPlaced.contains(block.getLocation())) {
                EvictingList<Location> pathCNM = pathW.get(block.getWorld().getName());
                if (pathCNM == null) {
                    for (World world : Bukkit.getWorlds()) {
                        pathW.put(world.getName(), new EvictingList<>(300));
                    }
                    pathCNM = pathW.get(block.getWorld().getName());
                }
                pathCNM.add(block.getLocation());

                if (!isInNaturalEnvironment(block.getLocation()) && !isSmoothPath(pathCNM)) {
                    if (isNewVein(block.getWorld().getName(), block.getLocation(), block.getType())) {
                        minedVein++;
                        lastVeinLocation.put(block.getWorld().getName(), block.getLocation());
                        int c = countVeinBlocks(block.getLocation(), block.getType());
                        debug("vein=" + minedVein + ", c=" + c);
                        if (minedVein >= veinThreshold) {
                            flag("xray.cluster", String.format("mining ore clusters suspicious, mined %s (clusters=%s, blocks=%s, turns=%s, bc=%s, yc=%s)", block.getType(), minedVein, c,
                                    totalTurns, branchCount, yChanges), 0.5);
                            minedVein = 0;
                        }
                    }
                }
            }
            playerPlaced.remove(block.getLocation());
        }
        if (event instanceof BlockPlaceEvent) {
            BlockPlaceEvent blockPlaceEvent = (BlockPlaceEvent) event;
            Block block = blockPlaceEvent.getBlock();
            if (isRareOre(block.getType())) {
                Location location = block.getLocation();
                playerPlaced.add(location);
            }
        }
    }


    private boolean isNewVein(String worldName, Location location, Material oreType) {
        Location lastLocation = lastVeinLocation.get(worldName);

        if (lastLocation == null || !isSameVein(lastLocation, location, oreType)) {
            lastVeinLocation.put(worldName, location);
            return true;
        }

        return false;
    }


    private boolean isSmoothPath(List<Location> path) {

        // Skidded from MinerTrack.
        // 是的所以我还是遵守了GPL协议

        Location lastLocation = null;
        Vector lastDirection = null;
        for (int i = 0; i < path.size(); i++) {
            Location currentLocation = path.get(i);
            if (lastLocation != null) {
                Vector currentDirection = currentLocation.toVector().subtract(lastLocation.toVector()).normalize();

                if (lastDirection != null) {
                    double dotProduct = lastDirection.dot(currentDirection);
                    if (dotProduct < Math.cos(Math.toRadians(30))) {
                        totalTurns++;
                    }
                }

                if (Math.abs(currentLocation.getY() - lastLocation.getY()) > 3) {
                    yChanges++;
                }

                if (i > 1) {
                    Location prevLocation = path.get(i - 1);
                    Vector prevDirection = prevLocation.toVector().subtract(lastLocation.toVector()).normalize();
                    if (currentDirection.angle(prevDirection) > Math.toRadians(60)) {
                        branchCount++;
                    }
                }

                lastDirection = currentDirection;
            }
            lastLocation = currentLocation;
        }

        return totalTurns < turnThreshold && branchCount < branchThreshold && yChanges < yChangeThreshold;
    }

    // 检查自然环境

    private boolean isInNaturalEnvironment(Location location) {

        int airCount = 0;
        int waterCount = 0;
        int lavaCount = 0;

        int detectionRange = enviDetectionRange;

        int waterThreshold = 14;
        int lavaThreshold = 14;

        int baseX = location.getBlockX();
        int baseY = location.getBlockY();
        int baseZ = location.getBlockZ();

        for (int x = -detectionRange; x <= detectionRange; x++) {
            for (int y = -detectionRange; y <= detectionRange; y++) {
                for (int z = -detectionRange; z <= detectionRange; z++) {
                    Material type = location.getWorld().getBlockAt(baseX + x, baseY + y, baseZ + z).getType();
                    switch (type) {
                        case AIR:
                            airCount++;
                            break;
                        case WATER:
                            waterCount++;
                            break;
                        case LAVA:
                            lavaCount++;
                            break;
                        default:
                            if (type.toString().contains("CAVE_AIR")) airCount += caveAirMultiplier;
                            else break;
                    }
                }
            }
        }

        if (airCount > airThreshold) return true;
        if (waterCount > waterThreshold) return true;
        if (lavaCount > lavaThreshold) return true;

        return false;
    }

    private boolean isSameVein(Location loc1, Location loc2, Material type) {
        if (!loc1.getWorld().equals(loc2.getWorld())) return false;
        if (!loc2.getBlock().getType().equals(type)) return false;

        double maxDistance = veinMaxDistance;
        Set<Location> visited = new HashSet<>();
        Queue<Location> toVisit = new LinkedList<>();
        toVisit.add(loc1);

        while (!toVisit.isEmpty()) {
            Location current = toVisit.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            if (current.distance(loc2) <= maxDistance) {
                return true;
            }
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        Location neighbor = current.clone().add(dx, dy, dz);
                        if (!visited.contains(neighbor) && neighbor.getBlock().getType().equals(type)) {
                            toVisit.add(neighbor);
                        }
                    }
                }
            }
        }
        return false;
    }

    public int countVeinBlocks(Location startLocation, Material type) {
        if (startLocation == null || !startLocation.getBlock().getType().equals(type)) {
            return 0;
        }

        double maxDistance = veinMaxDistance;
        Set<Location> visited = new HashSet<>();
        Queue<Location> toVisit = new LinkedList<>();
        toVisit.add(startLocation);

        int blockCount = 0;

        while (!toVisit.isEmpty()) {
            Location current = toVisit.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            if (current.getBlock().getType().equals(type)) {
                blockCount++;

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) continue;

                            Location neighbor = current.clone().add(dx, dy, dz);
                            if (!visited.contains(neighbor)
                                    && neighbor.distance(current) <= maxDistance
                                    && neighbor.getBlock().getType().equals(type)) {
                                toVisit.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
        return blockCount;
    }
}
