package com.polar.habbohotel.badges;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BadgeDefinition {
    private String code;
    private String requiredRight;
}
