package com.polar.habbohotel.achievements;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Achievement {
    private final int id;
    private final String groupName;
    private final String category;
    private final int gameId;
    private final Map<Integer, AchievementLevel> levels;

    public Achievement(int id, String groupName, String category, int gameId) {
        this.id = id;
        this.groupName = groupName;
        this.category = category;
        this.gameId = gameId;
        this.levels = new HashMap<>();
    }

    public void addLevel(AchievementLevel level) {
        levels.put(level.getLevel(), level);
    }
}
