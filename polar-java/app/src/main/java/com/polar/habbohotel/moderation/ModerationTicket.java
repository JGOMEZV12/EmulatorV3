package com.polar.habbohotel.moderation;

import com.polar.habbohotel.rooms.RoomData;
import com.polar.habbohotel.users.Habbo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModerationTicket {
    private int id;
    private int type;
    private int category;
    private double timestamp;
    private int priority;
    private boolean answered;
    private Habbo sender;
    private Habbo reported;
    private Habbo moderator;
    private String issue;
    private RoomData room;

    public ModerationTicket(int id, int type, int category, double timestamp, int priority, Habbo sender, Habbo reported, String issue, RoomData room) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.timestamp = timestamp;
        this.priority = priority;
        this.sender = sender;
        this.reported = reported;
        this.issue = issue;
        this.room = room;
        this.answered = false;
    }
}
