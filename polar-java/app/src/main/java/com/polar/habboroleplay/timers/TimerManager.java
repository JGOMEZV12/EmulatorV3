package com.polar.habboroleplay.timers;

import com.polar.habbohotel.gameclients.GameClient;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimerManager {

    private final GameClient client;
    private final Map<String, RoleplayTimer> activeTimers = new ConcurrentHashMap<>();

    public TimerManager(GameClient client) {
        this.client = client;
        initDefaultTimers();
    }

    private void initDefaultTimers() {
        // createTimer("hunger", 1000, true);
        // createTimer("hygiene", 1000, true);
    }

    public void createTimer(String type, int interval, boolean forever) {
        if (activeTimers.containsKey(type)) return;

        // Placeholder for specific timer creation logic
    }

    public void endAllTimers() {
        activeTimers.values().forEach(RoleplayTimer::stop);
        activeTimers.clear();
    }
}
