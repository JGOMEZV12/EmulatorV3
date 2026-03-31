package com.polar.habbohotel.permissions;

import com.polar.habbohotel.users.Habbo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PermissionManager {

    private final JdbcTemplate jdbcTemplate;
    private final Map<Integer, PermissionGroup> permissionGroups = new HashMap<>();
    private final Map<Integer, List<String>> permissionGroupRights = new HashMap<>();
    private final Map<Integer, List<String>> permissionSubscriptionRights = new HashMap<>();

    @Autowired
    public PermissionManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init() {
        log.info("Loading permissions...");
        permissionGroups.clear();
        permissionGroupRights.clear();
        permissionSubscriptionRights.clear();

        // Load Permission Groups
        jdbcTemplate.query("SELECT * FROM permissions_groups", rs -> {
            int id = rs.getInt("id");
            permissionGroups.put(id, new PermissionGroup("name", "description", "badge"));
        });

        // Load Permissions mapping
        Map<Integer, String> permissionsMap = new HashMap<>();
        jdbcTemplate.query("SELECT id, permission FROM permissions", rs -> {
            permissionsMap.put(rs.getInt("id"), rs.getString("permission"));
        });

        // Load Group Rights
        jdbcTemplate.query("SELECT group_id, permission_id FROM permissions_rights", rs -> {
            int groupId = rs.getInt("group_id");
            int permissionId = rs.getInt("permission_id");
            String permName = permissionsMap.get(permissionId);
            if (permName != null) {
                permissionGroupRights.computeIfAbsent(groupId, k -> new ArrayList<>()).add(permName);
            }
        });

        // Load Subscription Rights
        jdbcTemplate.query("SELECT subscription_id, permission_id FROM permissions_subscriptions", rs -> {
            int subId = rs.getInt("subscription_id");
            int permissionId = rs.getInt("permission_id");
            String permName = permissionsMap.get(permissionId);
            if (permName != null) {
                permissionSubscriptionRights.computeIfAbsent(subId, k -> new ArrayList<>()).add(permName);
            }
        });

        log.info("Loaded {} permission groups.", permissionGroups.size());
    }

    public List<String> getPermissionsForPlayer(Habbo player) {
        List<String> permissions = new ArrayList<>();

        List<String> groupRights = permissionGroupRights.get(player.getRank());
        if (groupRights != null) {
            permissions.addAll(groupRights);
        }

        List<String> subscriptionRights = permissionSubscriptionRights.get(player.getDiamonds());
        if (subscriptionRights != null) {
            permissions.addAll(subscriptionRights);
        }

        return permissions;
    }
}
