package com.polar.habboroleplay.timers;

import com.polar.habbohotel.gameclients.GameClient;
import lombok.Getter;

import java.util.Timer;
import java.util.TimerTask;

public abstract class RoleplayTimer {

    @Getter
    private final String type;
    @Getter
    private final GameClient client;
    private final int interval;
    private final boolean forever;
    private final Timer timer;

    public RoleplayTimer(String type, GameClient client, int interval, boolean forever) {
        this.type = type;
        this.client = client;
        this.interval = interval;
        this.forever = forever;
        this.timer = new Timer();
        start();
    }

    private void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    onTick();
                } catch (Exception e) {
                    stop();
                }
            }
        }, interval, interval);
    }

    public abstract void onTick();

    public void stop() {
        timer.cancel();
    }
}
