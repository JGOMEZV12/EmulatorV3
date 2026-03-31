package com.polar.habbohotel.groups;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rp_jobs") // Base table for groups/jobs
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(name = "desc")
    private String description;

    private String badge;

    @Column(name = "room_id")
    private int roomId;

    @Column(name = "owner_id")
    private int ownerId;

    private int created;
    private int state;
    private String colour1;
    private String colour2;

    @Column(name = "admindeco")
    private int adminOnlyDeco;

    @Column(name = "has_chat")
    private boolean hasChat;

    @Column(name = "bank_balance")
    private int balance;

    private int stock;
}
