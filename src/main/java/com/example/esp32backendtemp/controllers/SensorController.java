package com.example.esp32backendtemp.controllers;

import com.example.esp32backendtemp.models.Measurement;
import com.example.esp32backendtemp.models.Sensor;
import com.example.esp32backendtemp.repositories.MeasurementRepo;
import com.example.esp32backendtemp.repositories.SensorRepo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    private final SensorRepo sensorRepo;
    private final MeasurementRepo measurementRepo;

    public SensorController(SensorRepo sensorRepo, MeasurementRepo measurementRepo) {
        this.sensorRepo = sensorRepo;
        this.measurementRepo = measurementRepo;
    }

    //http://localhost:8080/sensor/getall
    @RequestMapping("/getall")
    public List<Sensor> getAll() {
        return sensorRepo.findAll();
    }

    //http://localhost:8080/sensor/getbyid/1
    @RequestMapping("/getbyid/{id}")
    public Sensor getById(@PathVariable Long id) {
        return sensorRepo.findById(id).
                orElseThrow(() -> new IllegalArgumentException("Invalid sensor ID"));
    }

    //http://localhost:8080/sensor/getbyname/vardagsrum
    @RequestMapping("/getbyname/{name}")
    public Sensor getByName(@PathVariable String name) {
        return sensorRepo.findByName(name);
    }

    //http://localhost:8080/sensor/add/sovrum
    @RequestMapping("/add/{name}")
    public String add(@PathVariable String name){
        sensorRepo.save(new Sensor(name));
        return "sensor " + name + " added";
    }

    //http://localhost:8080/sensor/1/measurements/2024-09-17
    @RequestMapping("/{sensorId}/measurements/{date}")
    public List<Measurement> getMeasurementsBySensorIdAndDate(@PathVariable Long sensorId, @PathVariable String date) {
        LocalDate measurementDate = LocalDate.parse(date);

        LocalDateTime startOfDay = measurementDate.atStartOfDay();
        LocalDateTime endOfDay = measurementDate.atTime(LocalTime.MAX);

        Sensor sensor = sensorRepo.findById(sensorId)
                .orElseThrow(() -> new RuntimeException("Sensor not found"));

        return measurementRepo.findBySensorIdAndMeasurementTimeBetween(sensor.getId(), startOfDay, endOfDay);
    }

    //http://localhost:8080/sensor/delete/
    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Sensor sensor = sensorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sensor ID"));

        String name = sensor.getName();
        sensorRepo.delete(sensor);

        return "sensor " + name + " deleted";
    }

}
