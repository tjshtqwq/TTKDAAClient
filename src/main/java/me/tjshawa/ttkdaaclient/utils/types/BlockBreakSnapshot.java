package me.tjshawa.ttkdaaclient.utils.types;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class BlockBreakSnapshot {
    private final UUID playerId;
    private final Material material;
    private final Location location;
    private final long timestamp;

    public BlockBreakSnapshot(BlockBreakEvent event) {
        this.playerId = event.getPlayer().getUniqueId();
        this.material = event.getBlock().getType();
        this.location = event.getBlock().getLocation().clone();
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public Material getMaterial() { return material; }
    public Location getLocation() { return location; }
    public UUID getPlayerId() { return playerId; }
}