package com.polar.habbohotel.permissions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PermissionGroup {
    private String name;
    private String description;
    private String badge;
}
