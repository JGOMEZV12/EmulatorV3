package com.polar.habboroleplay.rproom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RPRoomRepository extends JpaRepository<RPRoomEntity, Integer> {
}
