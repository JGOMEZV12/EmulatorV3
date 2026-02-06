package com.polar.habbohotel.subscriptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SubscriptionManager {

    private final Map<Integer, SubscriptionData> subscriptions = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading subscriptions...");
        // TODO: Database load logic
    }

    public SubscriptionData getSubscriptionData(int id) {
        return subscriptions.get(id);
    }
}
