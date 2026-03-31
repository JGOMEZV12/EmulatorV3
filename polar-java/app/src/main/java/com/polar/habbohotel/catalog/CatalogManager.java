package com.polar.habbohotel.catalog;

import com.polar.habbohotel.items.ItemData;
import com.polar.habbohotel.items.ItemDataManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CatalogManager {

    private final CatalogPageRepository pageRepository;
    private final CatalogItemRepository itemRepository;
    private final Map<Integer, CatalogPage> pages = new ConcurrentHashMap<>();
    private final Map<Integer, CatalogItem> offerItems = new ConcurrentHashMap<>();

    @Autowired
    public CatalogManager(CatalogPageRepository pageRepository, CatalogItemRepository itemRepository) {
        this.pageRepository = pageRepository;
        this.itemRepository = itemRepository;
    }

    public void init(ItemDataManager itemDataManager) {
        log.info("Loading catalog...");
        pages.clear();
        offerItems.clear();

        List<CatalogItemEntity> itemEntities = itemRepository.findAll();
        Map<Integer, Map<Integer, CatalogItem>> itemsByPage = new HashMap<>();

        for (CatalogItemEntity e : itemEntities) {
            ItemData data = itemDataManager.getItem(e.getItemId());
            if (data == null) {
                log.warn("Could not find furniture item {} for catalog item {}", e.getItemId(), e.getId());
                continue;
            }

            CatalogItem item = new CatalogItem(
                    e.getId(), e.getItemId(), data, e.getCatalogName(), e.getPageId(),
                    e.getCostCredits(), e.getCostPixels(), e.getCostDiamonds(), e.getAmount(),
                    e.getLimitedSells(), e.getLimitedStack(), e.isOfferActive(), e.getExtradata(),
                    e.getBadge(), e.getOfferId()
            );

            itemsByPage.computeIfAbsent(e.getPageId(), k -> new HashMap<>()).put(item.getId(), item);
            if (item.isOfferActive() && item.getOfferId() > 0) {
                offerItems.put(item.getOfferId(), item);
            }
        }

        List<CatalogPageEntity> pageEntities = pageRepository.findAll();
        for (CatalogPageEntity e : pageEntities) {
            Map<Integer, CatalogItem> pageItems = itemsByPage.getOrDefault(e.getId(), Map.of());
            Map<Integer, CatalogItem> pageItemOffers = pageItems.values().stream()
                    .filter(item -> item.getOfferId() > 0 && item.isOfferActive())
                    .collect(Collectors.toMap(CatalogItem::getOfferId, item -> item));

            CatalogPage page = new CatalogPage(
                    e.getId(), e.getParentId(), e.isEnabled() ? "1" : "0", e.getCaption(),
                    e.getPageLink(), e.getIconImage(), e.getMinRank(), e.getMinVip(),
                    e.isVisible() ? "1" : "0", e.getPageLayout(), e.getPageStrings1(),
                    e.getPageStrings2(), pageItems, pageItemOffers
            );
            pages.put(page.getId(), page);
        }

        log.info("Loaded {} catalog pages and {} active offers.", pages.size(), offerItems.size());
    }

    public CatalogPage getPage(int pageId) {
        return pages.get(pageId);
    }

    public CatalogItem getOffer(int offerId) {
        return offerItems.get(offerId);
    }
}
