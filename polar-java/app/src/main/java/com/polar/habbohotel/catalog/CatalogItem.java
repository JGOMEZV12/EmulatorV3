package com.polar.habbohotel.catalog;

import com.polar.habbohotel.items.ItemData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatalogItem {
    private int id;
    private int itemId;
    private ItemData data;
    private String catalogName;
    private int pageId;
    private int costCredits;
    private int costPixels;
    private int costDiamonds;
    private int amount;
    private int limitedSells;
    private int limitedStack;
    private boolean offerActive;
    private String extradata;
    private String badge;
    private int offerId;
}
