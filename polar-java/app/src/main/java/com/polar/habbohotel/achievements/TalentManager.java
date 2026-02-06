package com.polar.habbohotel.achievements;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TalentManager {

    private final Map<Integer, Talent> talents = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading talents...");
        // TODO: Database load logic
    }

    public Talent getTalent(int id) {
        return talents.get(id);
    }
}
