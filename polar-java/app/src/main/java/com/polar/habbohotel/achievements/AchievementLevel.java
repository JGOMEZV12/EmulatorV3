package com.polar.habbohotel.achievements;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AchievementLevel {
    private final int level;
    private final int rewardPixels;
    private final int rewardPoints;
    private final int requirement;
}
