package com.polar.habboroleplay.rproom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RPRoomManager {

    private final Map<String, RPRoom> hospitalRooms = new HashMap<>();
    private final Map<String, RPRoom> jailRooms = new HashMap<>();
    private final Map<String, RPRoom> courtRooms = new HashMap<>();

    public void init() {
        log.info("Loading RP rooms...");
        // TODO: Database load logic
    }

    public int getHospital(String city) {
        RPRoom room = hospitalRooms.get(city);
        return room != null ? room.getId() : 0;
    }
}
