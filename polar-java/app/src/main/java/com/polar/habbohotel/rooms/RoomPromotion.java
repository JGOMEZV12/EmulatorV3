package com.polar.habbohotel.rooms;

import com.polar.core.PolarEnvironment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomPromotion {
    private String name;
    private String description;
    private double timestampExpires;
    private double timestampStarted;
    private int categoryId;

    public RoomPromotion(String name, String desc, int categoryId) {
        this.name = name;
        this.description = desc;
        this.timestampStarted = PolarEnvironment.getUnixTimestamp();
        // Assuming a default lifetime if PolarStaticGameSettings is not yet available
        this.timestampExpires = this.timestampStarted + (120 * 60);
        this.categoryId = categoryId;
    }

    public RoomPromotion(String name, String desc, double started, double expires, int categoryId) {
        this.name = name;
        this.description = desc;
        this.timestampStarted = started;
        this.timestampExpires = expires;
        this.categoryId = categoryId;
    }

    public boolean hasExpired() {
        return (this.timestampExpires - PolarEnvironment.getUnixTimestamp()) < 0;
    }

    public int getMinutesLeft() {
        return (int) Math.ceil((this.timestampExpires - PolarEnvironment.getUnixTimestamp()) / 60.0);
    }
}
