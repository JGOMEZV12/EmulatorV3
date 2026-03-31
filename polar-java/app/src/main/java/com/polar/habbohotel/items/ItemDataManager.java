package com.polar.habbohotel.items;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemDataManager {

    private final FurnitureRepository furnitureRepository;
    private final Map<Integer, ItemData> items = new ConcurrentHashMap<>();
    private final Map<Integer, ItemData> gifts = new ConcurrentHashMap<>();

    @Autowired
    public ItemDataManager(FurnitureRepository furnitureRepository) {
        this.furnitureRepository = furnitureRepository;
    }

    public void init() {
        log.info("Loading furniture items...");
        List<Furniture> furnitureList = furnitureRepository.findAll();
        for (Furniture f : furnitureList) {
            List<Double> heightAdjustable = (f.getHeightAdjustable() != null && !f.getHeightAdjustable().isEmpty())
                    ? Arrays.stream(f.getHeightAdjustable().split(","))
                    .map(Double::valueOf)
                    .collect(Collectors.toList())
                    : List.of();

            ItemData data = new ItemData(
                    f.getId(), f.getSpriteId(), f.getItemName(), f.getPublicName(), f.getType(),
                    f.getWidth(), f.getLength(), f.getStackHeight(), f.isCanStack(), f.isIsWalkable(),
                    f.isCanSit(), f.isAllowRecycle(), f.isAllowTrade(), f.isAllowMarketplaceSell(),
                    f.isAllowGift(), f.isAllowInventoryStack(),
                    InteractionType.getTypeFromString(f.getInteractionType()),
                    f.getBehaviourData(), f.getInteractionModesCount(), f.getVendingIds(),
                    heightAdjustable, f.getEffectId(), f.isRare(), f.getClothingId(), f.isExtraRot()
            );

            items.put(f.getId(), data);
            if (!gifts.containsKey(f.getSpriteId())) {
                gifts.put(f.getSpriteId(), data);
            }
        }
        log.info("Loaded {} furniture items.", items.size());
    }

    public ItemData getItem(int id) {
        return items.get(id);
    }

    public ItemData getItemByName(String name) {
        return items.values().stream()
                .filter(item -> item.getItemName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
