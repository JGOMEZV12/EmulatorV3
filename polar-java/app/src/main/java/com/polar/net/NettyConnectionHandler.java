package com.polar.net;

import com.polar.habbohotel.gameclients.GameClient;
import com.polar.habbohotel.gameclients.GameClientManager;
import com.polar.net.codec.ClientPacket;
import com.polar.communication.packets.PacketManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyConnectionHandler extends ChannelInboundHandlerAdapter {

    private final GameClientManager clientManager;
    private final PacketManager packetManager;
    private GameClient client;

    public NettyConnectionHandler(GameClientManager clientManager, PacketManager packetManager) {
        this.clientManager = clientManager;
        this.packetManager = packetManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("New connection from {}", ctx.channel().remoteAddress());
        this.client = clientManager.createClient(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Connection closed from {}", ctx.channel().remoteAddress());
        if (client != null) {
            clientManager.removeClient(client.getId());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ClientPacket packet) {
            try {
                packetManager.tryExecutePacket(client, packet);
            } catch (Exception e) {
                log.error("Error handling packet", e);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in Netty handler", cause);
        ctx.close();
    }
}
