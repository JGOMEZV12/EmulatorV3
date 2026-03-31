package com.polar.habbohotel.quests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class QuestManager {

    private final QuestRepository questRepository;
    private final Map<Integer, Quest> quests = new ConcurrentHashMap<>();

    @Autowired
    public QuestManager(QuestRepository questRepository) {
        this.questRepository = questRepository;
    }

    public void init() {
        log.info("Loading quests...");
        quests.clear();
        List<QuestEntity> entities = questRepository.findAll();
        for (QuestEntity e : entities) {
            Quest quest = new Quest(
                    e.getId(), e.getType(), e.getLevelNum(), e.getGoalType(), e.getGoalData(),
                    e.getAction(), e.getReward(), e.getRewardType(), e.isRp()
            );
            quests.put(quest.getId(), quest);
        }
        log.info("Loaded {} quests.", quests.size());
    }

    public Quest getQuest(int id) {
        return quests.get(id);
    }
}
