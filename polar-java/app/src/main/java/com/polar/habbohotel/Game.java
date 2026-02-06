package com.polar.habbohotel;

import com.polar.habbohotel.catalog.CatalogManager;
import com.polar.habbohotel.gameclients.GameClientManager;
import com.polar.habbohotel.items.ItemDataManager;
import com.polar.habbohotel.rooms.RoomManager;
import com.polar.habbohotel.navigator.NavigatorManager;
import com.polar.habbohotel.landingview.LandingViewManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class Game {

    @Getter private final RoomManager roomManager;
    @Getter private final ItemDataManager itemDataManager;
    @Getter private final CatalogManager catalogManager;
    @Getter private final NavigatorManager navigatorManager;
    @Getter private final LandingViewManager landingViewManager;
    @Getter private final GameClientManager gameClientManager;

    @Autowired
    public Game(RoomManager roomManager, ItemDataManager itemDataManager,
                CatalogManager catalogManager, NavigatorManager navigatorManager,
                LandingViewManager landingViewManager, GameClientManager gameClientManager) {
        this.roomManager = roomManager;
        this.itemDataManager = itemDataManager;
        this.catalogManager = catalogManager;
        this.navigatorManager = navigatorManager;
        this.landingViewManager = landingViewManager;
        this.gameClientManager = gameClientManager;
    }

    @PostConstruct
    public void init() {
        log.info("Loading Game modules...");
        itemDataManager.init();
        catalogManager.init(itemDataManager);
        roomManager.loadModels();
        navigatorManager.init();
        landingViewManager.init();
    }

    @Scheduled(fixedDelay = 500)
    public void gameLoop() {
        try {
            // Process clients
            // gameClientManager.onCycle();

            // Process rooms
            roomManager.getRooms().values().forEach(room -> {
                try {
                    room.onCycle();
                } catch (Exception e) {
                    log.error("Error in room cycle for room {}", room.getId(), e);
                }
            });

        } catch (Exception e) {
            log.error("Error in main game loop", e);
        }
    }
}
