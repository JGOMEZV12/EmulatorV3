package com.polar.habbohotel.groups;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
public class GroupRank {
    private int jobId;
    private int rankId;
    private String name;
    private String maleFigure;
    private String femaleFigure;
    private int pay;
    private String[] commands;
    private String[] workRooms;
    private int limit;

    public boolean hasCommand(String command) {
        return Arrays.stream(commands).anyMatch(c -> c.equalsIgnoreCase(command));
    }

    public boolean canWorkHere(int workRoomId) {
        String roomIdStr = String.valueOf(workRoomId);
        return Arrays.stream(workRooms).anyMatch(r -> r.equals("*") || r.equals(roomIdStr));
    }
}
