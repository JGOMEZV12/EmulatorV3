package com.polar.habbohotel.poll;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<PollEntity, Integer> {
}
