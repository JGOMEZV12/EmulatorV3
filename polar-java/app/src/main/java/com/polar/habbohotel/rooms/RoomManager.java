package com.polar.habbohotel.rooms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RoomManager {

    private final RoomModelRepository roomModelRepository;
    private final Map<Integer, Room> rooms = new ConcurrentHashMap<>();
    private final Map<Integer, RoomData> loadedRoomData = new ConcurrentHashMap<>();
    private final Map<String, RoomModel> roomModels = new ConcurrentHashMap<>();

    @Autowired
    public RoomManager(RoomModelRepository roomModelRepository) {
        this.roomModelRepository = roomModelRepository;
    }

    public Map<Integer, Room> getRooms() {
        return rooms;
    }

    public void loadModels() {
        log.info("Loading room models...");
        roomModels.clear();
        List<RoomModelEntity> entities = roomModelRepository.findAll();
        for (RoomModelEntity e : entities) {
            RoomModel model = new RoomModel(
                    e.getId(), e.getDoorX(), e.getDoorY(), e.getDoorZ(), e.getDoorDir(),
                    e.getHeightmap(), e.getWallHeight(), e.getPoolmap()
            );
            roomModels.put(e.getId(), model);
        }
        log.info("Loaded {} room models.", roomModels.size());
    }

    public Room loadRoom(int id) {
        if (rooms.containsKey(id)) {
            return rooms.get(id);
        }

        RoomData data = loadedRoomData.get(id);
        if (data == null) {
            // TODO: Load from DB if not cached
            return null;
        }

        Room room = new Room(data);
        rooms.put(id, room);
        return room;
    }

    public Room getRoom(int id) {
        return rooms.get(id);
    }

    public void unloadRoom(int id) {
        Room room = rooms.remove(id);
        if (room != null) {
            room.dispose();
        }
    }
}
