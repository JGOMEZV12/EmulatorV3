package com.polar.habbohotel.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HabboRepository extends JpaRepository<Habbo, Integer> {
    Optional<Habbo> findByUsername(String username);
}
