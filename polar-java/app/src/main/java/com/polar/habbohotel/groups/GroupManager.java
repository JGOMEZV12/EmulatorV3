package com.polar.habbohotel.groups;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GroupManager {

    private final Map<Integer, Group> jobs = new ConcurrentHashMap<>();
    private final Map<Integer, Group> gangs = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading groups...");
        // TODO: Database load logic
    }

    public Group getGroup(int id) {
        if (id < 1000) return jobs.get(id);
        return gangs.get(id);
    }
}
