package com.polar.habbohotel.achievements;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TalentManager {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Integer, Talent> talents = new ConcurrentHashMap<>();

    @Autowired
    public TalentManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init() {
        log.info("Loading talents...");
        talents.clear();
        jdbcTemplate.query("SELECT * FROM talents_data ORDER BY order_num ASC", rs -> {
            Talent talent = new Talent(
                    rs.getInt("id"), rs.getString("type"), rs.getInt("parent_category"),
                    rs.getInt("level"), rs.getString("achievement_group"),
                    rs.getInt("achievement_level"), rs.getString("prize"),
                    rs.getInt("prize_baseitem")
            );
            talents.put(talent.getId(), talent);
        });
        log.info("Loaded {} talents.", talents.size());
    }

    public Talent getTalent(int id) {
        return talents.get(id);
    }
}
