package com.polar.habbohotel.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogPage {
    private int id;
    private int parentId;
    private String enabled;
    private String caption;
    private String pageLink;
    private int iconImage;
    private int minRank;
    private int minVip;
    private String visible;
    private String layout;
    private String strings1;
    private String strings2;
    private Map<Integer, CatalogItem> items;
    private Map<Integer, CatalogItem> itemOffers;
}
