package com.polar.habboroleplay.cooldowns;

import com.polar.habbohotel.gameclients.GameClient;
import lombok.Getter;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Cooldown {

    @Getter
    private final String type;
    @Getter
    private final GameClient client;
    private final int amount;
    @Getter
    private int timeLeft;
    private final Timer timer;

    public Cooldown(String type, GameClient client, int amount) {
        this.type = type;
        this.client = client;
        this.amount = amount;
        this.timeLeft = amount;
        this.timer = new Timer();
        start();
    }

    private void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                if (timeLeft <= 0) {
                    stop();
                    onFinish();
                }
            }
        }, 1000, 1000);
    }

    public abstract void onFinish();

    public void stop() {
        timer.cancel();
    }
}
