package com.polar.habbohotel.subscriptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SubscriptionManager {

    private final SubscriptionDataRepository repository;
    private final Map<Integer, SubscriptionData> subscriptions = new ConcurrentHashMap<>();

    @Autowired
    public SubscriptionManager(SubscriptionDataRepository repository) {
        this.repository = repository;
    }

    public void init() {
        log.info("Loading subscriptions...");
        subscriptions.clear();
        List<SubscriptionDataEntity> entities = repository.findAll();
        for (SubscriptionDataEntity e : entities) {
            SubscriptionData data = new SubscriptionData(
                    e.getId(), e.getName(), e.getBadge(), e.getCredits(), e.getDuckets(), e.getRespects()
            );
            subscriptions.put(data.getId(), data);
        }
        log.info("Loaded {} subscriptions.", subscriptions.size());
    }

    public SubscriptionData getSubscriptionData(int id) {
        return subscriptions.get(id);
    }
}
