package com.polar.habboroleplay.cooldowns;

import com.polar.habbohotel.gameclients.GameClient;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final GameClient client;
    private final Map<String, Cooldown> activeCooldowns = new ConcurrentHashMap<>();

    public CooldownManager(GameClient client) {
        this.client = client;
    }

    public void createCooldown(String type, int amount) {
        if (activeCooldowns.containsKey(type)) return;

        // Placeholder for specific cooldown creation logic
    }

    public boolean hasCooldown(String type) {
        return activeCooldowns.containsKey(type);
    }

    public void endAllCooldowns() {
        activeCooldowns.values().forEach(Cooldown::stop);
        activeCooldowns.clear();
    }
}
