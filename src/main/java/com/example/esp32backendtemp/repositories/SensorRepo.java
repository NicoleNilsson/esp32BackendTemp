package com.example.esp32backendtemp.repositories;

import com.example.esp32backendtemp.models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepo extends JpaRepository<Sensor, Long> {
    List<Sensor> findByName(String name);
}
