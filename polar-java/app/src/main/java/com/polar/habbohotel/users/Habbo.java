package com.polar.habbohotel.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Habbo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    private int rank;
    private String motto;
    private String look;
    private String gender;
    private int credits;

    @Column(name = "activity_points")
    private int duckets;

    @Column(name = "vip_points")
    private int diamonds;

    @Column(name = "home_room")
    private int homeRoom;

    @Column(name = "last_online")
    private int lastOnline;

    private boolean online;

    @Column(name = "machine_id")
    private String machineId;

    // TODO: Add more fields and components (Messenger, Inventory, etc.)
}
