package com.polar.habbohotel.moderation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ModerationManager {

    private final List<String> userPresets = new ArrayList<>();
    private final List<String> roomPresets = new ArrayList<>();
    private final Map<String, ModerationBan> bans = new ConcurrentHashMap<>();
    private final Map<Integer, ModerationTicket> modTickets = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading moderation module...");
        // TODO: Database load logic
    }

    public void addBan(ModerationBan ban) {
        bans.put(ban.getValue(), ban);
    }

    public ModerationBan getBan(String value) {
        return bans.get(value);
    }
}
