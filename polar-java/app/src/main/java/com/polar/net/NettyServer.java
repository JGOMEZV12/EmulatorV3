package com.polar.net;

import com.polar.habbohotel.gameclients.GameClientManager;
import com.polar.communication.packets.PacketManager;
import com.polar.net.codec.HabboPacketDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyServer {

    @Value("${game.tcp.port:3000}")
    private int port;

    private final GameClientManager clientManager;
    private final PacketManager packetManager;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Autowired
    public NettyServer(GameClientManager clientManager, PacketManager packetManager) {
        this.clientManager = clientManager;
        this.packetManager = packetManager;
    }

    @PostConstruct
    public void start() {
        new Thread(() -> {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new HabboPacketDecoder());
                                ch.pipeline().addLast(new NettyConnectionHandler(clientManager, packetManager));
                            }
                        });

                log.info("Starting Netty server on port {}", port);
                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("Netty server interrupted", e);
                Thread.currentThread().interrupt();
            } finally {
                stop();
            }
        }).start();
    }

    @PreDestroy
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
