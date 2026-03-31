package com.polar.habbohotel.users;

import com.polar.habbohotel.achievements.UserAchievement;
import com.polar.habbohotel.subscriptions.Subscription;
import com.polar.habbohotel.users.messenger.HabboMessenger;
import com.polar.habboroleplay.roleplayusers.RoleplayUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    @Transient
    private HabboMessenger messenger;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private RoleplayUser roleplayUser;

    @Transient
    private final Map<String, UserAchievement> achievements = new ConcurrentHashMap<>();

    @Transient
    private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

    public void init() {
        this.messenger = new HabboMessenger(id);
    }

    public UserAchievement getAchievementData(String group) {
        return achievements.get(group);
    }
}
