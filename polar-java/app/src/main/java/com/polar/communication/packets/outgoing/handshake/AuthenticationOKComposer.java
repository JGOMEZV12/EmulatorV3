package com.polar.communication.packets.outgoing.handshake;

import com.polar.communication.packets.Composer;
import com.polar.communication.packets.ServerPacket;
import com.polar.communication.packets.ServerPacketHeader;

public class AuthenticationOKComposer implements Composer {

    @Override
    public ServerPacket compose() {
        return new ServerPacket(ServerPacketHeader.AUTHENTICATION_OK);
    }
}
