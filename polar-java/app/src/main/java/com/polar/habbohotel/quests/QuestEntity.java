package com.polar.habbohotel.quests;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quests")
public class QuestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String type;

    @Column(name = "level_num")
    private int levelNum;

    @Column(name = "goal_type")
    private int goalType;

    @Column(name = "goal_data")
    private int goalData;

    private String action;

    @Column(name = "pixel_reward")
    private int reward;

    @Column(name = "data_bit")
    private String dataBit;

    @Column(name = "reward_type")
    private int rewardType;

    @Column(name = "timestamp_unlock")
    private int timestampUnlock;

    @Column(name = "timestamp_lock")
    private int timestampLock;

    @Column(name = "is_rp")
    private boolean isRp;
}
