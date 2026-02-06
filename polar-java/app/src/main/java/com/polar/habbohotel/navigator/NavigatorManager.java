package com.polar.habbohotel.navigator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NavigatorManager {

    private final Map<Integer, Object> featuredRooms = new ConcurrentHashMap<>();
    private final Map<Integer, Object> searchResultLists = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading navigator...");
        // TODO: Database load logic
    }
}
