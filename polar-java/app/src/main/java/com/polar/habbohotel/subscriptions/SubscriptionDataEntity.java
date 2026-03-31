package com.polar.habbohotel.subscriptions;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "subscriptions")
public class SubscriptionDataEntity {

    @Id
    private int id;

    private String name;

    @Column(name = "badge_code")
    private String badge;

    private int credits;
    private int duckets;
    private int respects;
}
