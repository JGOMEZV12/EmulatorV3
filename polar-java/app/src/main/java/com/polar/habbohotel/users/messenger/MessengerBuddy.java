package com.polar.habbohotel.users.messenger;

import com.polar.communication.packets.ServerPacket;
import com.polar.habbohotel.gameclients.GameClient;
import com.polar.habbohotel.rooms.Room;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessengerBuddy {
    private int userId;
    private String username;
    private String look;
    private String motto;
    private int lastOnline;
    private boolean appearOffline;
    private boolean hideInRoom;
    private boolean isBot;
    private int groupId;

    private GameClient client;
    private Room currentRoom;

    public MessengerBuddy(int userId, String username, String look, String motto, int lastOnline,
                          boolean appearOffline, boolean hideInRoom, boolean isBot) {
        this.userId = userId;
        this.username = username;
        this.look = look;
        this.motto = motto;
        this.lastOnline = lastOnline;
        this.appearOffline = appearOffline;
        this.hideInRoom = hideInRoom;
        this.isBot = isBot;
    }

    public boolean isOnline() {
        return (client != null && client.getHabbo() != null && client.getHabbo().getMessenger() != null &&
                !client.getHabbo().getMessenger().isAppearOffline()) || isBot;
    }

    public boolean isInRoom() {
        return currentRoom != null;
    }

    public void serialize(ServerPacket message, GameClient session) {
        // Relationship logic simplified for now
        int relationshipType = 0;

        message.writeInt(userId);
        message.writeString(username);
        message.writeInt(1); // Online status (1 = online, but depends on boolean below)
        message.writeBoolean(isOnline() && (!appearOffline || session.getHabbo().getRank() >= 5));
        message.writeBoolean(isInRoom() && (!hideInRoom || session.getHabbo().getRank() >= 5));
        message.writeString(isOnline() || isBot ? look : "");
        message.writeInt(0); // category id
        message.writeString(motto);
        message.writeString(""); // Facebook username
        message.writeString("");
        message.writeBoolean(true); // Allows offline messaging
        message.writeBoolean(false);
        message.writeBoolean(false); // Uses phone
        message.writeShort(relationshipType);
    }
}
