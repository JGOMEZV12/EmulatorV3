package com.polar.habboroleplay.roleplayusers;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rp_stats")
public class RoleplayUser {

    @Id
    private int id;

    private int level;

    @Column(name = "level_exp")
    private int levelExp;

    @Column(name = "job_id")
    private int jobId;

    @Column(name = "job_rank")
    private int jobRank;

    @Column(name = "curhealth")
    private int curHealth;

    @Column(name = "maxhealth")
    private int maxHealth;

    @Column(name = "curenergy")
    private int curEnergy;

    @Column(name = "maxenergy")
    private int maxEnergy;

    private int hunger;
    private int armor;
    private int intelligence;
    private int strength;
    private int stamina;

    @Column(name = "is_dead")
    private boolean isDead;

    @Column(name = "is_jailed")
    private boolean isJailed;

    @Column(name = "is_wanted")
    private boolean isWanted;

    @Column(name = "wanted_level")
    private int wantedLevel;

    @Column(name = "bank_chequings")
    private int bankChequings;

    @Column(name = "bank_savings")
    private int bankSavings;

    // TODO: Add remaining fields from rp_stats table
}
