package com.polar.communication.packets;

import com.polar.habbohotel.gameclients.GameClient;
import com.polar.net.codec.ClientPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PacketManager {

    private final Map<Short, PacketEvent> incomingPackets = new HashMap<>();

    public PacketManager() {
        registerHandshake();
    }

    private void registerHandshake() {
        // incomingPackets.put((short) 4000, new GetClientVersionEvent());
    }

    public void tryExecutePacket(GameClient session, ClientPacket packet) {
        PacketEvent event = incomingPackets.get(packet.getHeader());

        if (event == null) {
            log.warn("Unknown packet received: header={}", packet.getHeader());
            return;
        }

        try {
            event.parse(session, packet);
        } catch (Exception e) {
            log.error("Error executing packet header={}", packet.getHeader(), e);
        }
    }
}
