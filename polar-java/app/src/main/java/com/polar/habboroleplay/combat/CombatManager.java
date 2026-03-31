package com.polar.habboroleplay.combat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CombatManager {

    private final Map<String, ICombat> combatTypes = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading combat manager...");
        // combatTypes.put("fist", new Fist());
        // combatTypes.put("gun", new Gun());
    }

    public ICombat getCombatType(String type) {
        return combatTypes.get(type.toLowerCase());
    }
}
