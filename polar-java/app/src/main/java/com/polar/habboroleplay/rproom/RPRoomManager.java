package com.polar.habboroleplay.rproom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RPRoomManager {

    private final RPRoomRepository rpRoomRepository;
    private final Map<String, RPRoom> hospitalRooms = new ConcurrentHashMap<>();
    private final Map<String, RPRoom> jailRooms = new ConcurrentHashMap<>();
    private final Map<String, RPRoom> courtRooms = new ConcurrentHashMap<>();

    @Autowired
    public RPRoomManager(RPRoomRepository rpRoomRepository) {
        this.rpRoomRepository = rpRoomRepository;
    }

    public void init() {
        log.info("Loading RP rooms...");
        hospitalRooms.clear();
        jailRooms.clear();
        courtRooms.clear();

        List<RPRoomEntity> entities = rpRoomRepository.findAll();
        for (RPRoomEntity e : entities) {
            RPRoom room = new RPRoom(
                    e.getId(), e.getCity(), e.isHospital(), e.isPrison(), e.isCourt(),
                    e.isPrisonBack(), e.isCamionero(), e.isMecanico(), e.isBasurero(),
                    e.isArmero(), e.isPolStation()
            );

            if (e.isHospital()) hospitalRooms.put(e.getCity(), room);
            if (e.isPrison()) jailRooms.put(e.getCity(), room);
            if (e.isCourt()) courtRooms.put(e.getCity(), room);
        }
        log.info("Loaded {} hospital rooms, {} jail rooms, and {} court rooms.",
                hospitalRooms.size(), jailRooms.size(), courtRooms.size());
    }

    public int getHospital(String city) {
        RPRoom room = hospitalRooms.get(city);
        return room != null ? room.getId() : 0;
    }
}
