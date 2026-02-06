package com.polar.habbohotel.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ItemDataManager {

    private final Map<Integer, ItemData> items = new ConcurrentHashMap<>();
    private final Map<Integer, ItemData> gifts = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading items...");
        // TODO: Database load logic
    }

    public ItemData getItem(int id) {
        return items.get(id);
    }

    public ItemData getItemByName(String name) {
        return items.values().stream()
                .filter(item -> item.getItemName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
