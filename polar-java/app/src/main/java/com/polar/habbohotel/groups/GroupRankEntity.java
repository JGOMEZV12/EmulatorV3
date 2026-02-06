package com.polar.habbohotel.groups;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rp_jobs_ranks")
public class GroupRankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "job")
    private int jobId;

    private int rank;
    private String name;

    @Column(name = "male_figure")
    private String maleFigure;

    @Column(name = "female_figure")
    private String femaleFigure;

    private int pay;
    private String commands;
    private String workrooms;
    private int limit;
}
