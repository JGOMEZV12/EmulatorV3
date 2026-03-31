package com.polar.habbohotel.poll;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "polls")
public class PollEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "room_id")
    private int roomId;

    private String type;

    @Column(name = "poll_name")
    private String pollName;

    @Column(name = "poll_invitation")
    private String pollInvitation;

    @Column(name = "poll_thanks")
    private String pollThanks;

    @Column(name = "poll_prize")
    private String pollPrize;
}
