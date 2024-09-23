package com.example.esp32backendtemp.repositories;

import com.example.esp32backendtemp.models.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepo extends JpaRepository<Measurement, Long> {
    List<Measurement> findByMeasurementTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Measurement> findBySensorIdAndMeasurementTimeBetween(Long sensorId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
