package com.polar.habbohotel.users.messenger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessengerRequest {
    private final int toUser;
    private final int fromUser;
    private final String username;
}
