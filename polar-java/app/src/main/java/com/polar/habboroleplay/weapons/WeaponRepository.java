package com.polar.habboroleplay.weapons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Integer> {
    Optional<Weapon> findByName(String name);
}
