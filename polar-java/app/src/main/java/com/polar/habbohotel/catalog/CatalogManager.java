package com.polar.habbohotel.catalog;

import com.polar.habbohotel.items.ItemDataManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CatalogManager {

    private final Map<Integer, CatalogPage> pages = new ConcurrentHashMap<>();
    private final Map<Integer, CatalogItem> offerItems = new ConcurrentHashMap<>();

    public void init(ItemDataManager itemDataManager) {
        log.info("Loading catalog...");
        // TODO: Database load logic
    }

    public CatalogPage getPage(int pageId) {
        return pages.get(pageId);
    }

    public CatalogItem getOffer(int offerId) {
        return offerItems.get(offerId);
    }
}
