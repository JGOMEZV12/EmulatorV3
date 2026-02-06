package com.polar.habbohotel.subscriptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionDataRepository extends JpaRepository<SubscriptionDataEntity, Integer> {
}
