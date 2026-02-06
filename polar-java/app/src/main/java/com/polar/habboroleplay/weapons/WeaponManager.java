package com.polar.habboroleplay.weapons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WeaponManager {

    private final WeaponRepository weaponRepository;
    private final Map<String, Weapon> weapons = new ConcurrentHashMap<>();

    @Autowired
    public WeaponManager(WeaponRepository weaponRepository) {
        this.weaponRepository = weaponRepository;
    }

    public void init() {
        log.info("Loading roleplay weapons...");
        List<Weapon> weaponList = weaponRepository.findAll();
        for (Weapon weapon : weaponList) {
            weapons.put(weapon.getName().toLowerCase(), weapon);
        }
        log.info("Loaded {} roleplay weapons.", weapons.size());
    }

    public Weapon getWeapon(String name) {
        return weapons.get(name.toLowerCase());
    }

    public List<Weapon> getAllWeapons() {
        return List.copyOf(weapons.values());
    }
}
