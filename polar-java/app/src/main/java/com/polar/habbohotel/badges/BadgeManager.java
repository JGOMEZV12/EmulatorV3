package com.polar.habbohotel.badges;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class BadgeManager {

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, BadgeDefinition> badges = new ConcurrentHashMap<>();

    @Autowired
    public BadgeManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init() {
        log.info("Loading badges...");
        badges.clear();
        jdbcTemplate.query("SELECT * FROM badge_definitions", rs -> {
            String code = rs.getString("code").toUpperCase();
            String requiredRight = rs.getString("required_right");
            badges.put(code, new BadgeDefinition(code, requiredRight));
        });
        log.info("Loaded {} badge definitions.", badges.size());
    }

    public BadgeDefinition getBadge(String code) {
        return badges.get(code.toUpperCase());
    }
}
