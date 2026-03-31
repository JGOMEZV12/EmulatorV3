package com.polar.habbohotel.gameclients;

import com.polar.habbohotel.users.Habbo;
import com.polar.net.codec.ClientPacket;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameClient {

    @Getter
    private final int id;

    @Getter
    private final Channel channel;

    @Getter @Setter
    private Habbo habbo;

    @Getter @Setter
    private String machineId;

    public GameClient(int id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    public void sendPacket(Object packet) {
        if (channel.isActive()) {
            channel.writeAndFlush(packet);
        }
    }

    public void disconnect() {
        if (channel.isActive()) {
            channel.close();
        }
    }

    public void handlePacket(ClientPacket packet) {
        // TODO: Send to PacketManager
        log.debug("Handling packet {} for client {}", packet.getHeader(), id);
    }
}
