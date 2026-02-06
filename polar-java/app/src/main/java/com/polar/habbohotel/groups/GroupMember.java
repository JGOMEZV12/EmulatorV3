package com.polar.habbohotel.groups;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupMember {
    private int groupId;
    private int userId;
    private int userRank;
    private boolean isAdmin;
}
