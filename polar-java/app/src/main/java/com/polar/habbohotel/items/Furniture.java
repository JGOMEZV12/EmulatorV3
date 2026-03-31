package com.polar.habbohotel.items;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "furniture")
public class Furniture {

    @Id
    private int id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "public_name")
    private String publicName;

    private String type;
    private int width;
    private int length;

    @Column(name = "stack_height")
    private double stackHeight;

    @Column(name = "can_stack")
    private boolean canStack;

    @Column(name = "is_walkable")
    private boolean isWalkable;

    @Column(name = "can_sit")
    private boolean canSit;

    @Column(name = "allow_recycle")
    private boolean allowRecycle;

    @Column(name = "allow_trade")
    private boolean allowTrade;

    @Column(name = "allow_marketplace_sell")
    private boolean allowMarketplaceSell;

    @Column(name = "allow_gift")
    private boolean allowGift;

    @Column(name = "allow_inventory_stack")
    private boolean allowInventoryStack;

    @Column(name = "interaction_type")
    private String interactionType;

    @Column(name = "behaviour_data")
    private int behaviourData;

    @Column(name = "interaction_modes_count")
    private int interactionModesCount;

    @Column(name = "vending_ids")
    private String vendingIds;

    @Column(name = "height_adjustable")
    private String heightAdjustable;

    @Column(name = "effect_id")
    private int effectId;

    @Column(name = "is_rare")
    private boolean isRare;

    @Column(name = "clothing_id")
    private int clothingId;

    @Column(name = "extra_rot")
    private boolean extraRot;

    @Column(name = "sprite_id")
    private int spriteId;
}
