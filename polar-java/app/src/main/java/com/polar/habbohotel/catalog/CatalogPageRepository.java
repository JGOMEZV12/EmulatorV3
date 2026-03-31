package com.polar.habbohotel.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogPageRepository extends JpaRepository<CatalogPageEntity, Integer> {
}
