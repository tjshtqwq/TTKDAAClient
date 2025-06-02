package me.tjshawa.ttkdaaclient.manager;

import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
