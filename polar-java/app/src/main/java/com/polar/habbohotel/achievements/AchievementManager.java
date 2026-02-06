package com.polar.habbohotel.achievements;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AchievementManager {

    private final Map<String, Achievement> achievements = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading achievements...");
        // TODO: Database load logic
    }

    public Achievement getAchievement(String group) {
        return achievements.get(group);
    }
}
