package com.polar.habbohotel.subscriptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionData {
    private int id;
    private String name;
    private String badge;
    private int credits;
    private int duckets;
    private int respects;
}
