package com.polar.habboroleplay.vehicles;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class VehicleManager {

    private final VehicleRepository vehicleRepository;
    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> enables = new ConcurrentHashMap<>();

    @Autowired
    public VehicleManager(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public void init() {
        log.info("Loading roleplay vehicles...");
        List<Vehicle> vehicleList = vehicleRepository.findAll();
        for (Vehicle vehicle : vehicleList) {
            vehicles.put(vehicle.getModel(), vehicle);
            enables.put(vehicle.getEffectId(), vehicle.getId());
        }
        log.info("Loaded {} roleplay vehicles.", vehicles.size());
    }

    public Vehicle getVehicle(String model) {
        return vehicles.get(model);
    }

    public List<Vehicle> getAllVehicles() {
        return List.copyOf(vehicles.values());
    }
}
