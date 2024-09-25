package com.example.esp32backendtemp.services;

import com.example.esp32backendtemp.models.Measurement;
import com.example.esp32backendtemp.models.Sensor;
import com.example.esp32backendtemp.repositories.MeasurementRepo;
import com.example.esp32backendtemp.repositories.SensorRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorStatusChecker {

	private final SensorRepo sensorRepo;
	private final MeasurementRepo measurementRepo;

	// Time interval after which the sensor should be marked as offline - 15min
	private static final Duration OFFLINE_THRESHOLD = Duration.ofMinutes(15);

	public SensorStatusChecker(SensorRepo sensorRepo, MeasurementRepo measurementRepo) {
		this.sensorRepo = sensorRepo;
		this.measurementRepo = measurementRepo;
	}

	// Runs every 5 minutes
	@Scheduled(fixedRate = 5 * 60 * 1000)
	@Transactional
	public void checkSensorStatus() {
		List<Sensor> sensors = sensorRepo.findAll();

		for (Sensor sensor : sensors) {
			Measurement latestMeasurement = measurementRepo.findFirstBySensorOrderByMeasurementTimeDesc(sensor);

			if (latestMeasurement != null) {
				Duration durationSinceLastMeasurement = Duration.between(latestMeasurement.getMeasurementTime(), LocalDateTime.now());

				// If it's been more than 15 minutes, set the sensor as offline
				if (durationSinceLastMeasurement.compareTo(OFFLINE_THRESHOLD) > 0) {
					if (sensor.isStatus()) {  // Only update if it's currently online
						sensor.setStatus(false);  // Set sensor to offline
						sensorRepo.save(sensor);  // Save the updated sensor status
						System.out.println("Sensor " + sensor.getName() + " set to offline.");
					}
				}
			} else {
				if (sensor.isStatus()) {
					sensor.setStatus(false);
					sensorRepo.save(sensor);
					System.out.println("Sensor " + sensor.getName() + " has no measurements and is set to offline.");
				}
			}
		}
	}
}
