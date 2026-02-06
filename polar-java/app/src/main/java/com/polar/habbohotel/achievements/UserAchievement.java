package com.polar.habbohotel.achievements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAchievement {
    private final String achievementGroup;
    private int level;
    private int progress;
}
