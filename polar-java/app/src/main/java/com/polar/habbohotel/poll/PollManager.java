package com.polar.habbohotel.poll;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PollManager {

    private final Map<Integer, Poll> polls = new ConcurrentHashMap<>();

    public void init() {
        log.info("Loading polls...");
        // TODO: Database load logic
    }

    public Poll getPoll(int id) {
        return polls.get(id);
    }
}
