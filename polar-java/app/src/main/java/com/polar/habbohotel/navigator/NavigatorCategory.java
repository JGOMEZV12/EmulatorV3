package com.polar.habbohotel.navigator;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "navigator_categories")
public class NavigatorCategory {

    @Id
    private int id;

    private String category;

    @Column(name = "category_identifier")
    private String categoryIdentifier;

    @Column(name = "public_name")
    private String publicName;

    private boolean enabled;

    @Column(name = "required_rank")
    private int requiredRank;

    @Column(name = "view_mode")
    private String viewMode;

    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "search_allowance")
    private String searchAllowance;

    @Column(name = "order_id")
    private int orderId;
}
