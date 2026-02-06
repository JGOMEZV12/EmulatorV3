package com.polar.habbohotel.gameclients;

import io.netty.channel.Channel;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GameClientManager {

    private final ConcurrentHashMap<Integer, GameClient> clients = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public GameClient createClient(Channel channel) {
        int id = idCounter.getAndIncrement();
        GameClient client = new GameClient(id, channel);
        clients.put(id, client);
        return client;
    }

    public void removeClient(int id) {
        clients.remove(id);
    }

    public GameClient getClient(int id) {
        return clients.get(id);
    }

    public int getClientCount() {
        return clients.size();
    }
}
