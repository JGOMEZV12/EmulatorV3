package com.polar.habboroleplay.weapons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rp_weapons")
public class Weapon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(name = "public_name")
    private String publicName;

    @Column(name = "firing_text")
    private String firingText;

    @Column(name = "equip_text")
    private String equipText;

    @Column(name = "unequip_text")
    private String unEquipText;

    @Column(name = "reload_text")
    private String reloadText;

    private int energy;

    @Column(name = "effect_id")
    private int effectId;

    @Column(name = "hand_item")
    private int handItem;

    private int range;

    @Column(name = "min_damage")
    private int minDamage;

    @Column(name = "max_damage")
    private int maxDamage;

    @Column(name = "clip_size")
    private int clipSize;

    @Column(name = "reload_time")
    private int reloadTime;

    private int cost;

    @Column(name = "cost_fine")
    private int costFine;

    private int stock;

    @Column(name = "level_requirement")
    private int levelRequirement;

    @Transient
    private boolean canUse;

    @Transient
    private int totalBullets;

    @Transient
    private int wLife;

    @Column(name = "is_vip")
    private boolean isVip;

    @Transient
    private int baulCar;
}
