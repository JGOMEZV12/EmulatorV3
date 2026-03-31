package com.polar.habbohotel.rooms;

public enum SquareState {
    OPEN(0),
    BLOCKED(1),
    SEAT(2),
    POOL(3),
    VIP(4);

    private final int value;

    SquareState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
