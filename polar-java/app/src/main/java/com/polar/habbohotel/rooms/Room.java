package com.polar.habbohotel.rooms;

import lombok.Getter;
import lombok.Setter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Getter
@Setter
public class Room extends RoomData {

    private boolean disposed;
    private final Map<Integer, Double> bans = new ConcurrentHashMap<>();
    private final Map<Integer, Double> mutedUsers = new ConcurrentHashMap<>();

    // Managers (simplified for now)
    // private RoomUserManager userManager;
    // private RoomItemHandler itemHandler;

    public Room(RoomData data) {
        this.setId(data.getId());
        this.setName(data.getName());
        this.setDescription(data.getDescription());
        this.setOwnerId(data.getOwnerId());
        this.setOwnerName(data.getOwnerName());
        this.setState(data.getState());
        this.setCategory(data.getCategory());
        this.setUsersMax(data.getUsersMax());
        this.setModelName(data.getModelName());
        this.setScore(data.getScore());
        this.setTags(data.getTags());
        this.setAllowPets(data.isAllowPets());
        this.setAllowPetsEating(data.isAllowPetsEating());
        this.setRoomBlockingEnabled(data.isRoomBlockingEnabled());
        this.setHidewall(data.isHidewall());
        this.setPassword(data.getPassword());
        this.setWallpaper(data.getWallpaper());
        this.setFloor(data.getFloor());
        this.setLandscape(data.getLandscape());

        // RP fields
        this.setCity(data.getCity());
        this.setSafeZoneEnabled(data.isSafeZoneEnabled());

        this.disposed = false;
    }

    public void onCycle() {
        if (disposed) return;
        // Logic for room cycle
    }

    public void dispose() {
        this.disposed = true;
        // Cleanup logic
    }
}
