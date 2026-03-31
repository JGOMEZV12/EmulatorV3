package com.polar.habbohotel.moderation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ModerationManager {

    private final JdbcTemplate jdbcTemplate;
    private final List<String> userPresets = new ArrayList<>();
    private final List<String> roomPresets = new ArrayList<>();
    private final Map<String, ModerationBan> bans = new ConcurrentHashMap<>();
    private final Map<Integer, ModerationTicket> modTickets = new ConcurrentHashMap<>();

    @Autowired
    public ModerationManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init() {
        log.info("Loading moderation module...");
        userPresets.clear();
        roomPresets.clear();
        bans.clear();

        jdbcTemplate.query("SELECT * FROM moderation_presets", rs -> {
            String type = rs.getString("type").toLowerCase();
            String message = rs.getString("message");
            if (type.equals("user")) userPresets.add(message);
            else if (type.equals("room")) roomPresets.add(message);
        });

        jdbcTemplate.query("SELECT bantype, value, reason, expire FROM bans WHERE bantype = 'machine' OR bantype = 'user'", rs -> {
            String typeStr = rs.getString("bantype");
            String value = rs.getString("value");
            String reason = rs.getString("reason");
            double expire = rs.getDouble("expire");

            ModerationBanType type = typeStr.equalsIgnoreCase("machine") ? ModerationBanType.MACHINE : ModerationBanType.USERNAME;
            bans.put(value, new ModerationBan(type, value, reason, expire));
        });

        log.info("Loaded {} user presets, {} room presets, and {} bans.",
                userPresets.size(), roomPresets.size(), bans.size());
    }

    public void addBan(ModerationBan ban) {
        bans.put(ban.getValue(), ban);
    }

    public ModerationBan getBan(String value) {
        return bans.get(value);
    }
}
