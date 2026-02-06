package com.polar.habbohotel.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItemEntity, Integer> {
    List<CatalogItemEntity> findByOfferActiveTrue();
}
