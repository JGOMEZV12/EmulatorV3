package com.polar.habbohotel.permissions;

import com.polar.habbohotel.users.Habbo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PermissionManager {

    private final Map<Integer, PermissionGroup> permissionGroups = new HashMap<>();
    private final Map<Integer, List<String>> permissionGroupRights = new HashMap<>();
    private final Map<Integer, List<String>> permissionSubscriptionRights = new HashMap<>();

    public void init() {
        log.info("Loading permissions...");
        // TODO: Database load logic
    }

    public List<String> getPermissionsForPlayer(Habbo player) {
        List<String> permissions = new ArrayList<>();

        List<String> groupRights = permissionGroupRights.get(player.getRank());
        if (groupRights != null) {
            permissions.addAll(groupRights);
        }

        List<String> subscriptionRights = permissionSubscriptionRights.get(player.getDiamonds()); // Using diamonds for VIP rank temporarily
        if (subscriptionRights != null) {
            permissions.addAll(subscriptionRights);
        }

        return permissions;
    }
}
