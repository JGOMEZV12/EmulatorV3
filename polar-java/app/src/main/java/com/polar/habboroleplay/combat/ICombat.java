package com.polar.habboroleplay.combat;

import com.polar.habbohotel.gameclients.GameClient;

public interface ICombat {
    void execute(GameClient session, GameClient target);
}
