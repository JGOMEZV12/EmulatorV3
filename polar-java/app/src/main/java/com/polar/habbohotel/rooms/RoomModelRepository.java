package com.polar.habbohotel.rooms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomModelRepository extends JpaRepository<RoomModelEntity, String> {
}
