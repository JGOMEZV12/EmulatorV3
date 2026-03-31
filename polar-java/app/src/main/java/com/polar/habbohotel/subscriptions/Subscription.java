package com.polar.habbohotel.subscriptions;

import com.polar.core.PolarEnvironment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Subscription {
    private final String caption;
    private int timeExpire;
    private final int activateTimes;

    public boolean isValid() {
        return timeExpire > PolarEnvironment.getUnixTimestamp();
    }

    public void extendSubscription(int time) {
        this.timeExpire += time;
    }
}
