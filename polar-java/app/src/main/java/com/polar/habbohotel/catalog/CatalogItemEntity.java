package com.polar.habbohotel.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "catalog_items")
public class CatalogItemEntity {

    @Id
    private int id;

    @Column(name = "item_id")
    private int itemId;

    @Column(name = "catalog_name")
    private String catalogName;

    @Column(name = "cost_credits")
    private int costCredits;

    @Column(name = "cost_pixels")
    private int costPixels;

    @Column(name = "cost_diamonds")
    private int costDiamonds;

    private int amount;

    @Column(name = "page_id")
    private int pageId;

    @Column(name = "limited_sells")
    private int limitedSells;

    @Column(name = "limited_stack")
    private int limitedStack;

    @Column(name = "offer_active")
    private boolean offerActive;

    private String extradata;
    private String badge;

    @Column(name = "offer_id")
    private int offerId;
}
