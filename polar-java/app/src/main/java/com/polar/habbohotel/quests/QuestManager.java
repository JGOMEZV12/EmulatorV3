package com.polar.habbohotel.quests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class QuestManager {

    private final Map<Integer, Quest> quests = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading quests...");
        // TODO: Database load logic
    }

    public Quest getQuest(int id) {
        return quests.get(id);
    }
}
