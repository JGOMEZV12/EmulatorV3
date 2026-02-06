package com.polar.habbohotel.achievements;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AchievementManager {

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, Achievement> achievements = new ConcurrentHashMap<>();

    @Autowired
    public AchievementManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init() {
        log.info("Loading achievements...");
        achievements.clear();

        jdbcTemplate.query("SELECT * FROM achievements", rs -> {
            int id = rs.getInt("id");
            String groupName = rs.getString("group_name");
            String category = rs.getString("category");
            int gameId = rs.getInt("game_id");

            Achievement ach = new Achievement(id, groupName, category, gameId);
            achievements.put(groupName, ach);
        });

        jdbcTemplate.query("SELECT * FROM achievement_levels", rs -> {
            String groupName = rs.getString("achievement_group");
            Achievement ach = achievements.get(groupName);
            if (ach != null) {
                AchievementLevel level = new AchievementLevel(
                        rs.getInt("level"), rs.getInt("reward_pixels"),
                        rs.getInt("reward_points"), rs.getInt("requirement")
                );
                ach.addLevel(level);
            }
        });

        log.info("Loaded {} achievements.", achievements.size());
    }

    public Achievement getAchievement(String group) {
        return achievements.get(group);
    }
}
