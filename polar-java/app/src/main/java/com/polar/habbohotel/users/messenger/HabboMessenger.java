package com.polar.habbohotel.users.messenger;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HabboMessenger {
    private final int userId;

    @Getter @Setter
    private boolean appearOffline;

    private final Map<Integer, MessengerBuddy> friends;
    private final Map<Integer, MessengerRequest> requests;

    public HabboMessenger(int userId) {
        this.userId = userId;
        this.friends = new ConcurrentHashMap<>();
        this.requests = new ConcurrentHashMap<>();
    }

    public void init(Map<Integer, MessengerBuddy> friends, Map<Integer, MessengerRequest> requests) {
        this.friends.putAll(friends);
        this.requests.putAll(requests);
    }

    public Map<Integer, MessengerBuddy> getFriends() {
        return friends;
    }

    public Map<Integer, MessengerRequest> getRequests() {
        return requests;
    }

    public void updateFriend(int userId, boolean notification) {
        // Update logic
    }
}
