package com.polar.habbohotel.poll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class Poll {
    private final int id;
    private final int roomId;
    private final String pollName;
    private final String pollInvitation;
    private final String thanks;
    private final String prize;
    private final int type;
    private final List<Object> questions; // Simplified for now
}
