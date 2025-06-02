package me.tjshawa.ttkdaaclient.check;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import me.tjshawa.ttkdaaclient.utils.types.PlayerData;

public class GenericPacketHandle extends Check {
    public GenericPacketHandle(PlayerData data) {
        super(data);
    }

    @Override
    public void run(Object event) {
        if (event instanceof PacketReceiveEvent) {
            PacketReceiveEvent packetEvent = (PacketReceiveEvent) event;
            if (packetEvent.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {
                WrapperPlayClientInteractEntity entity =  new WrapperPlayClientInteractEntity(packetEvent);
                 if (entity.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                    data.useEntityTicks = 0;
                    data.target = entity.getEntityId();
                }
            }
            // 判定Flying包（包括pos之类的），pe有内置方法
            if (WrapperPlayClientPlayerFlying.isFlying(packetEvent.getPacketType())) {
                WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(packetEvent);
                data.useEntityTicks++;
                data.lastLastGround = data.lastGround;
                data.lastGround = data.ground;
                data.ground = flying.isOnGround();
                data.joinTicks++;
                if (flying.hasPositionChanged()) {
                    double x = flying.getLocation().getX();
                    double y = flying.getLocation().getY();
                    double z = flying.getLocation().getZ();
                    double yaw = flying.getLocation().getYaw();
                    double pitch = flying.getLocation().getPitch();
                    data.lastX = data.x;
                    data.lastY = data.y;
                    data.lastZ = data.z;
                    data.x = x;
                    data.y = y;
                    data.z = z;
                    data.lastDeltaX = data.deltaX;
                    data.lastDeltaY = data.deltaY;
                    data.lastDeltaZ = data.deltaZ;
                    data.deltaX = Math.abs(data.x - data.lastX);
                    data.deltaY = data.y - data.lastY;
                    data.deltaZ = Math.abs(data.z - data.lastZ);
                    data.deltaXZ = Math.hypot(data.deltaX, data.deltaZ);
                    data.joltX = Math.abs(data.deltaX - data.lastDeltaX);
                    data.joltY = Math.abs(data.deltaY - data.lastDeltaY);
                    data.joltZ = Math.abs(data.deltaZ - data.lastDeltaZ);

                    data.lastYaw = data.yaw;
                    data.lastPitch = data.pitch;
                    data.yaw = yaw;
                    data.pitch = pitch;
                    data.lastDeltaYaw = data.deltaYaw;
                    data.lastDeltaPitch = data.deltaPitch;
                    data.deltaYaw = Math.abs(data.yaw - data.lastYaw);
                    data.deltaPitch = Math.abs(data.pitch - data.lastPitch);
                    data.joltYaw = Math.abs(data.deltaYaw - data.lastDeltaYaw);
                    data.joltPitch = Math.abs(data.deltaPitch - data.lastDeltaPitch);
                }
            }
        }
    }
}
