package com.polar.habboroleplay.rproom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RPRoom {
    private int id;
    private String city;
    private boolean isHospital;
    private boolean isPrison;
    private boolean isCourt;
    private boolean isPrisonBack;
    private boolean isCamionero;
    private boolean isMecanico;
    private boolean isBasurero;
    private boolean isArmero;
    private boolean isPolStation;
}
