package com.polar.habbohotel.navigator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NavigatorCategoryRepository extends JpaRepository<NavigatorCategory, Integer> {
    List<NavigatorCategory> findByEnabledTrueOrderByOrderIdAsc();
}
