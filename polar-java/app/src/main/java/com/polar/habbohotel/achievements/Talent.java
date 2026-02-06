package com.polar.habbohotel.achievements;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Talent {
    private final int id;
    private final String type;
    private final int parentCategory;
    private final int level;
    private final String achievementGroup;
    private final int achievementLevel;
    private final String prize;
    private final int prizeBaseItem;
}
