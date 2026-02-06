package com.polar.habbohotel.rooms;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class RoomData {
    private int id;
    private String name;
    private String description;
    private String type;
    private int ownerId;
    private String ownerName;
    private int state;
    private int category;
    private int usersNow;
    private int usersMax;
    private String modelName;
    private int score;
    private List<String> tags = new ArrayList<>();
    private boolean allowPets;
    private boolean allowPetsEating;
    private boolean roomBlockingEnabled;
    private boolean hidewall;
    private String password;
    private String wallpaper;
    private String floor;
    private String landscape;
    private int floorThickness;
    private int wallThickness;
    private int whoCanMute;
    private int whoCanKick;
    private int whoCanBan;
    private int chatMode;
    private int chatSpeed;
    private int chatSize;
    private int tradeSettings;
    private int groupId;

    // RP Fields
    private String city;
    private boolean bankEnabled;
    private boolean shootEnabled;
    private boolean hitEnabled;
    private boolean safeZoneEnabled;
    private boolean robEnabled;
    private boolean gymEnabled;
    private String enterRoomMessage;

    private RoomPromotion promotion;

    public boolean hasActivePromotion() {
        return promotion != null && !promotion.hasExpired();
    }
}
