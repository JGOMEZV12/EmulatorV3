package com.polar.habboroleplay.roleplayusers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleplayUserRepository extends JpaRepository<RoleplayUser, Integer> {
}
