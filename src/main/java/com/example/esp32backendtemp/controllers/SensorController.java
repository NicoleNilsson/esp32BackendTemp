package com.example.esp32backendtemp.controllers;

import com.example.esp32backendtemp.exceptions.SensorNotFoundException;
import com.example.esp32backendtemp.models.Measurement;
import com.example.esp32backendtemp.models.Sensor;
import com.example.esp32backendtemp.repositories.MeasurementRepo;
import com.example.esp32backendtemp.repositories.SensorRepo;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping("/getbyid/{sensorId}")
    public Sensor getById(@PathVariable Long sensorId) {
        return sensorRepo.findById(sensorId).
                orElseThrow(() -> new SensorNotFoundException(String.valueOf(sensorId), "ID"));
    }

    //http://localhost:8080/sensor/getbyname/vardagsrum
    @RequestMapping("/getbyname/{name}")
    public Sensor getByName(@PathVariable String name) {
        Sensor sensor = sensorRepo.findByName(name);
        if (sensor == null) {throw new SensorNotFoundException(name, "name");}
        return sensorRepo.findByName(name);
    }

    //http://localhost:8080/sensor/add/sovrum
    @RequestMapping("/add/{name}")
    public String add(@PathVariable String name){
        sensorRepo.save(new Sensor(name));
        return "sensor " + name + " added";
    }

    //http://localhost:8080/sensor/getbyid/1/measurements/2024-09-18
    @RequestMapping("/getbyid/{sensorId}/measurements/{date}")
    public List<Measurement> getMeasurementsBySensorIdAndDate(@PathVariable Long sensorId, @PathVariable String date) {
        LocalDate measurementDate = LocalDate.parse(date);
        LocalDateTime startOfDay = measurementDate.atStartOfDay();
        LocalDateTime endOfDay = measurementDate.atTime(LocalTime.MAX);

        Sensor sensor = sensorRepo.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(String.valueOf(sensorId), "ID"));

        return measurementRepo.findBySensorIdAndMeasurementTimeBetween(sensor.getId(), startOfDay, endOfDay);
    }

    //http://localhost:8080/sensor/getbyname/vardagsrum/measurements/2024-09-18
    @RequestMapping("/getbyname/{name}/measurements/{date}")
    public List<Measurement> getMeasurementsBySensorNameAndDate(@PathVariable String name,
                                                                @PathVariable String date) {

        LocalDate measurementDate = LocalDate.parse(date);
        LocalDateTime startOfDay = measurementDate.atStartOfDay();
        LocalDateTime endOfDay = measurementDate.atTime(LocalTime.MAX);

        Sensor sensor = sensorRepo.findByName(name);
        if (sensor == null) {throw new SensorNotFoundException(name, "name");}

        return measurementRepo.findBySensorIdAndMeasurementTimeBetween(sensor.getId(), startOfDay, endOfDay);
    }

    //http://localhost:8080/sensor/getbyname/vardagsrum/measurements/2024-09-18/2024-09-19
    @RequestMapping("/getbyname/{name}/measurements/{startDate}/{endDate}")
    public List<Measurement> getMeasurementsBySensorNameAndDateRange(@PathVariable String name,
                                                                     @PathVariable String startDate,
                                                                     @PathVariable String endDate) {

        LocalDateTime startTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        Sensor sensor = sensorRepo.findByName(name);
        if (sensor == null) {throw new SensorNotFoundException(name, "name");}

        return measurementRepo.findBySensorIdAndMeasurementTimeBetween(sensor.getId(), startTime, endTime);
    }

    //http://localhost:8080/sensor/delete/
    @RequestMapping("/delete/{sensorId}")
    public String delete(@PathVariable Long sensorId) {
        Sensor sensor = sensorRepo.findById(sensorId)
                .orElseThrow(() -> new SensorNotFoundException(String.valueOf(sensorId), "ID"));

        String name = sensor.getName();
        sensorRepo.delete(sensor);

        return "Sensor " + name + " deleted";
    }

    @PutMapping("/update")
    public String update(@RequestBody Sensor sensor) {
        Sensor existingSensor = sensorRepo.findById(sensor.getId())
                .orElseThrow(() -> new SensorNotFoundException(String.valueOf(sensor.getId()), "ID"));

        sensorRepo.save(existingSensor);
        return "sensor " + existingSensor.getName() + " updated";
    }
}
