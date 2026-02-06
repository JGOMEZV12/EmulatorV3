package com.polar.habbohotel.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "catalog_pages")
public class CatalogPageEntity {

    @Id
    private int id;

    @Column(name = "parent_id")
    private int parentId;

    private String caption;

    @Column(name = "page_link")
    private String pageLink;

    private boolean visible;
    private boolean enabled;

    @Column(name = "min_rank")
    private int minRank;

    @Column(name = "min_vip")
    private int minVip;

    @Column(name = "icon_image")
    private int iconImage;

    @Column(name = "page_layout")
    private String pageLayout;

    @Column(name = "page_strings_1")
    private String pageStrings1;

    @Column(name = "page_strings_2")
    private String pageStrings2;

    @Column(name = "order_num")
    private int orderNum;
}
