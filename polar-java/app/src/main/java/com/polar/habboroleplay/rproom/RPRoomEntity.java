package com.polar.habboroleplay.rproom;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rp_rooms")
public class RPRoomEntity {

    @Id
    private int id;

    private String city;

    @Column(name = "is_hospital")
    private boolean isHospital;

    @Column(name = "is_prison")
    private boolean isPrison;

    @Column(name = "is_court")
    private boolean isCourt;

    @Column(name = "is_prisonback")
    private boolean isPrisonBack;

    @Column(name = "is_camionero")
    private boolean isCamionero;

    @Column(name = "is_mecanico")
    private boolean isMecanico;

    @Column(name = "is_basurero")
    private boolean isBasurero;

    @Column(name = "is_armero")
    private boolean isArmero;

    @Column(name = "is_polstation")
    private boolean isPolStation;
}
