package com.polar.habbohotel.quests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestRepository extends JpaRepository<QuestEntity, Integer> {
}
