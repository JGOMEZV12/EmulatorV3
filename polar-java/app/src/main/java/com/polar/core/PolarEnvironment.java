package com.polar.core;

import com.polar.database.DatabaseManager;
import com.polar.habbohotel.Game;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class PolarEnvironment {

    public static final String PRETTY_VERSION = "Polar Server RP";
    public static final String PRETTY_BUILD = "2.1.2";

    @Getter
    private static Game game;
    @Getter
    private static DatabaseManager databaseManager;

    private final Game injectedGame;
    private final DatabaseManager injectedDatabaseManager;

    @Autowired
    public PolarEnvironment(Game game, DatabaseManager databaseManager) {
        this.injectedGame = game;
        this.injectedDatabaseManager = databaseManager;
    }

    @PostConstruct
    public void init() {
        game = injectedGame;
        databaseManager = injectedDatabaseManager;

        log.info("Initializing {} v{}", PRETTY_VERSION, PRETTY_BUILD);

        if (databaseManager.isConnected()) {
            log.info("Connected to database successfully!");
        } else {
            log.error("Failed to connect to database!");
        }
    }

    public static int getUnixTimestamp() {
        return (int) (System.currentTimeMillis() / 1000L);
    }
}
