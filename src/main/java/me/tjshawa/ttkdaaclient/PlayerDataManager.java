package me.tjshawa.ttkdaaclient;

import me.tjshawa.ttkdaaclient.types.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerDataManager {
    public static Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    public static void addPlayerData(UUID uuid, Player pp) {
        playerDataMap.put(uuid, new PlayerData(pp));
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public static void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
    }
}
