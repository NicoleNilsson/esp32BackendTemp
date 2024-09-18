package com.example.esp32backendtemp.repositories;

import com.example.esp32backendtemp.models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepo extends JpaRepository<Sensor, Long> {
    Sensor findByName(String name);
}
