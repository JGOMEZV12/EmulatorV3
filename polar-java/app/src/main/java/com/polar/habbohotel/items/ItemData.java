package com.polar.habbohotel.items;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemData {
    private int id;
    private int spriteId;
    private String itemName;
    private String publicName;
    private String type;
    private int width;
    private int length;
    private double height;
    private boolean allowStack;
    private boolean allowWalk;
    private boolean allowSit;
    private boolean allowRecycle;
    private boolean allowTrade;
    private boolean allowMarketplace;
    private boolean allowGift;
    private boolean allowInventoryStack;
    private InteractionType interactionType;
    private int behaviourData;
    private int cycleCount;
    private String vendingIds;
    private List<Double> heightAdjustable;
    private int effectId;
    private boolean isRare;
    private int clothingId;
    private boolean extraRot;
}
