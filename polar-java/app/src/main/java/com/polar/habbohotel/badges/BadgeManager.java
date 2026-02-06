package com.polar.habbohotel.badges;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class BadgeManager {

    private final Map<String, BadgeDefinition> badges = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading badges...");
        // TODO: Database load logic
    }

    public BadgeDefinition getBadge(String code) {
        return badges.get(code.toUpperCase());
    }
}
