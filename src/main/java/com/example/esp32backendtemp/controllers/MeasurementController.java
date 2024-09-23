package com.example.esp32backendtemp.controllers;

import com.example.esp32backendtemp.DTOs.MeasurementDTO;
import com.example.esp32backendtemp.exceptions.SensorNotFoundException;
import com.example.esp32backendtemp.models.Measurement;
import com.example.esp32backendtemp.repositories.MeasurementRepo;
import com.example.esp32backendtemp.models.Sensor;
import com.example.esp32backendtemp.repositories.SensorRepo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/measurement")
public class MeasurementController {

    private final MeasurementRepo measurementRepo;
    private final SensorRepo sensorRepo;

    public MeasurementController(MeasurementRepo measurementRepo, SensorRepo sensorRepo) {
        this.measurementRepo = measurementRepo;
        this.sensorRepo = sensorRepo;
    }

    //http://localhost:8080/measurement/getall
    @RequestMapping("/getall")
    public List<Measurement> getAll() {
        return measurementRepo.findAll();
    }

    //http://localhost:8080/measurement/getbydate/2024-09-17
    @RequestMapping("/getbydate/{date}")
    public List<Measurement> getByDate(@PathVariable String date) {
        LocalDate measurementDate = LocalDate.parse(date);
        LocalDateTime startOfDay = measurementDate.atStartOfDay();
        LocalDateTime endOfDay = measurementDate.atTime(LocalTime.MAX);

        return measurementRepo.findByMeasurementTimeBetween(startOfDay, endOfDay);
    }

    @PostMapping("/add")
    public String add(@RequestBody MeasurementDTO data) {
        Sensor sensor = sensorRepo.findById(data.getSensorId())
                .orElseThrow(() -> new SensorNotFoundException(String.valueOf(data.getSensorId()), "ID"));

        Measurement measurement = new Measurement(data.getTemp(), sensor);
        sensor.addMeasurement(measurement);
        measurementRepo.save(measurement);

        return "measurement added to sensor " + sensor.getName();
    }

    //http://localhost:8080/measurement/delete/
    @RequestMapping("/delete/{measurementId}")
    public String delete(@PathVariable Long measurementId) {
        Measurement measurement = measurementRepo.findById(measurementId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid measurement ID"));

        measurementRepo.delete(measurement);

        return "measurement deleted";
    }
}
