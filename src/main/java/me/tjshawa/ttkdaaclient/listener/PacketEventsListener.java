package me.tjshawa.ttkdaaclient.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import me.tjshawa.ttkdaaclient.manager.PlayerDataManager;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PacketEventsListener implements PacketListener {
    // 注册

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        User user = event.getUser();
        Player pp = event.getPlayer();
        if (pp == null) return;
        UUID ppUUID = pp.getUniqueId();
        PlayerData data = PlayerDataManager.getPlayerData(ppUUID);
        if (data != null) {
            data.checks.forEach(check -> check.run(event));
        }
    }
}
