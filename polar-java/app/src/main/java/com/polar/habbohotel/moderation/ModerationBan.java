package com.polar.habbohotel.moderation;

import com.polar.core.PolarEnvironment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModerationBan {
    private final ModerationBanType type;
    private final String value;
    private final String reason;
    private final double expire;

    public boolean isExpired() {
        return PolarEnvironment.getUnixTimestamp() >= expire;
    }
}
