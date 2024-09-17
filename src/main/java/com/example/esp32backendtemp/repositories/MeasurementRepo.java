package com.example.esp32backendtemp.repositories;

import com.example.esp32backendtemp.models.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepo extends JpaRepository<Measurement, Long> {
}
