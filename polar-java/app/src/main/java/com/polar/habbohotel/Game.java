package com.polar.habbohotel;

import com.polar.habbohotel.achievements.AchievementManager;
import com.polar.habbohotel.achievements.TalentManager;
import com.polar.habbohotel.badges.BadgeManager;
import com.polar.habbohotel.catalog.CatalogManager;
import com.polar.habbohotel.gameclients.GameClientManager;
import com.polar.habbohotel.items.ItemDataManager;
import com.polar.habbohotel.landingview.LandingViewManager;
import com.polar.habbohotel.moderation.ModerationManager;
import com.polar.habbohotel.navigator.NavigatorManager;
import com.polar.habbohotel.poll.PollManager;
import com.polar.habbohotel.quests.QuestManager;
import com.polar.habbohotel.rooms.RoomManager;
import com.polar.habbohotel.groups.GroupManager;
import com.polar.habbohotel.permissions.PermissionManager;
import com.polar.habbohotel.subscriptions.SubscriptionManager;
import com.polar.habboroleplay.rproom.RPRoomManager;
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
    @Getter private final GroupManager groupManager;
    @Getter private final PermissionManager permissionManager;
    @Getter private final RPRoomManager rpRoomManager;
    @Getter private final AchievementManager achievementManager;
    @Getter private final TalentManager talentManager;
    @Getter private final BadgeManager badgeManager;
    @Getter private final SubscriptionManager subscriptionManager;
    @Getter private final ModerationManager moderationManager;
    @Getter private final PollManager pollManager;
    @Getter private final QuestManager questManager;

    @Autowired
    public Game(RoomManager roomManager, ItemDataManager itemDataManager,
                CatalogManager catalogManager, NavigatorManager navigatorManager,
                LandingViewManager landingViewManager, GameClientManager gameClientManager,
                GroupManager groupManager, PermissionManager permissionManager,
                RPRoomManager rpRoomManager, AchievementManager achievementManager,
                TalentManager talentManager, BadgeManager badgeManager,
                SubscriptionManager subscriptionManager, ModerationManager moderationManager,
                PollManager pollManager, QuestManager questManager) {
        this.roomManager = roomManager;
        this.itemDataManager = itemDataManager;
        this.catalogManager = catalogManager;
        this.navigatorManager = navigatorManager;
        this.landingViewManager = landingViewManager;
        this.gameClientManager = gameClientManager;
        this.groupManager = groupManager;
        this.permissionManager = permissionManager;
        this.rpRoomManager = rpRoomManager;
        this.achievementManager = achievementManager;
        this.talentManager = talentManager;
        this.badgeManager = badgeManager;
        this.subscriptionManager = subscriptionManager;
        this.moderationManager = moderationManager;
        this.pollManager = pollManager;
        this.questManager = questManager;
    }

    @PostConstruct
    public void init() {
        log.info("Loading Game modules...");
        itemDataManager.init();
        catalogManager.init(itemDataManager);
        roomManager.loadModels();
        navigatorManager.init();
        landingViewManager.init();
        groupManager.init();
        permissionManager.init();
        rpRoomManager.init();
        achievementManager.init();
        talentManager.init();
        badgeManager.init();
        subscriptionManager.init();
        moderationManager.init();
        pollManager.init();
        questManager.init();
    }

    @Scheduled(fixedDelay = 500)
    public void gameLoop() {
        try {
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
