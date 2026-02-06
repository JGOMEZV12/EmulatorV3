package com.polar.habbohotel.groups;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Group {
    private int id;
    private String name;
    private String description;
    private String badge;
    private int roomId;
    private int creatorId;
    private String colour1;
    private String colour2;
    private int adminOnlyDeco;

    private int gangKills;
    private int gangDeaths;
    private int balance;
    private boolean isGang;
    private int stock;

    private final Map<Integer, GroupRank> ranks = new ConcurrentHashMap<>();
    private final Map<Integer, GroupMember> members = new ConcurrentHashMap<>();

    public Group(int id, String name, String description, String badge, int roomId, int ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.badge = badge;
        this.roomId = roomId;
        this.creatorId = ownerId;
    }

    public boolean isMember(int userId) {
        return members.containsKey(userId);
    }

    public boolean isAdmin(int userId) {
        GroupMember member = members.get(userId);
        return member != null && member.isAdmin();
    }
}
