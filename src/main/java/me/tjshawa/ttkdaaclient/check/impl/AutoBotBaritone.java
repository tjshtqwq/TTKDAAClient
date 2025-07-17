package me.tjshawa.ttkdaaclient.check.impl;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import me.tjshawa.ttkdaaclient.check.Check;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;

public class AutoBotBaritone extends Check {
    public AutoBotBaritone(PlayerData data) {
        super(data);
        this.name = "AutoBotBaritone";
        this.configName = "autobot-baritone";
    }
    private int breakTicks = 0;

    @Override
    public void run(Object event) {
        if (!isEnabled()) return;
        if (event instanceof PacketReceiveEvent) {
            PacketReceiveEvent packetEvent = (PacketReceiveEvent) event;
            if (packetEvent.getPacketType() == PacketType.Play.Client.PLAYER_DIGGING) {
                WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(packetEvent);
                if (wrapper.getAction() == DiggingAction.FINISHED_DIGGING) {
                    breakTicks = 0;
                    debug("reset " + breakTicks);
                }
            }
            if (WrapperPlayClientPlayerFlying.isFlying(packetEvent.getPacketType())) {
                WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(packetEvent);
                if (flying.hasRotationChanged() && ++breakTicks < 10) {
                    final double deltaYaw = data.deltaYaw;
                    final double deltaPitch = data.deltaPitch;
                    final boolean invalid = deltaYaw < 0.005 && deltaPitch < 0.005 && deltaYaw > 0.0 && deltaPitch > 0.0;
                    if (invalid) {
                        if (addBuffer() > 2) {
                            flag("aim.bot.a", String.format("rotated machine-like during digging (dY=%.5f, dP=%.5f)", deltaYaw, deltaPitch));
                        }
                    }
                }
                if (flying.hasRotationChanged() && ++breakTicks < 5) {
                    final double deltaYaw = data.deltaYaw;
                    final double deltaPitch = data.deltaPitch;
                    final double pitch = data.pitch;
                    final boolean invalid = deltaYaw > 95.0f && deltaPitch < 0.15 && Math.abs(pitch) < 70.0f;
                    if (invalid) {
                        if (addBuffer() > 2) {
                            flag("aim.bot.b", String.format("rotated machine-like during digging (dY=%.5f, dP=%.5f, p=%.5f)", deltaYaw, deltaPitch, pitch));
                        }
                    }
                }
            }
        }
    }
}
