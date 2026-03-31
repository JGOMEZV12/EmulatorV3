package com.polar.habbohotel.quests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Quest {
    private final int id;
    private final String category;
    private final int number;
    private final int goalType;
    private final int goalData;
    private final String name;
    private final int reward;
    private final int rewardType;
    private final boolean isRP;
}
