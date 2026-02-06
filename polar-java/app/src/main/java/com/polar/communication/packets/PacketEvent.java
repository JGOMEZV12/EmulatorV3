package com.polar.communication.packets;

import com.polar.habbohotel.gameclients.GameClient;
import com.polar.net.codec.ClientPacket;

public interface PacketEvent {
    void parse(GameClient session, ClientPacket packet) throws Exception;
}
