package com.polar.habbohotel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class Game {

    // TODO: Add all managers (Catalog, Room, etc.)

    @PostConstruct
    public void init() {
        log.info("Loading Game modules...");
        // Initialize managers here
    }

    public void startGameLoop() {
        // Implement game loop logic
    }
}
