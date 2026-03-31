package com.polar.habboroleplay.vehicles;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rp_vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "item_id")
    private int itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "effect_id")
    private int effectId;

    private int price;
    private String model;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "max_fuel")
    private int maxFuel;

    @Column(name = "max_trunks")
    private int maxTrunks;

    @Column(name = "type")
    private int carType;

    @Column(name = "max_passengers")
    private int maxDoors;

    @Column(name = "jobid")
    private int carCorp;

    @Column(name = "fast")
    private int fastCar;
}
