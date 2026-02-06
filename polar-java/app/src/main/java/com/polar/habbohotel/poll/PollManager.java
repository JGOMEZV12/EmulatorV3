package com.polar.habbohotel.poll;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PollManager {

    private final PollRepository pollRepository;
    private final Map<Integer, Poll> polls = new ConcurrentHashMap<>();

    @Autowired
    public PollManager(PollRepository pollRepository) {
        this.pollRepository = pollRepository;
    }

    public void init() {
        log.info("Loading polls...");
        polls.clear();
        List<PollEntity> entities = pollRepository.findAll();
        for (PollEntity e : entities) {
            Poll poll = new Poll(
                    e.getId(), e.getRoomId(), e.getPollName(), e.getPollInvitation(),
                    e.getPollThanks(), e.getPollPrize(), 0, List.of()
            );
            polls.put(poll.getId(), poll);
        }
        log.info("Loaded {} polls.", polls.size());
    }

    public Poll getPoll(int id) {
        return polls.get(id);
    }
}
