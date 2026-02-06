package com.polar.habbohotel.rooms;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "room_models")
public class RoomModelEntity {

    @Id
    private String id;

    @Column(name = "door_x")
    private int doorX;

    @Column(name = "door_y")
    private int doorY;

    @Column(name = "door_z")
    private double doorZ;

    @Column(name = "door_dir")
    private int doorDir;

    private String heightmap;

    @Column(name = "wall_height")
    private int wallHeight;

    private String poolmap;

    private boolean custom;
}
