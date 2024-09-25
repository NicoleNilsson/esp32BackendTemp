package com.example.esp32backendtemp.controllers;

import com.example.esp32backendtemp.DTOs.MeasurementDTO;
import com.example.esp32backendtemp.exceptions.SensorNotFoundException;
import com.example.esp32backendtemp.models.Measurement;
import com.example.esp32backendtemp.repositories.MeasurementRepo;
import com.example.esp32backendtemp.models.Sensor;
import com.example.esp32backendtemp.repositories.SensorRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    @Value("${esp32.ip}")
    private String espIp;

    @Value("${esp32.port}")
    private int espPort;

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

        if (!sensor.isStatus())
        {
            sensor.setStatus(true);
            sensorRepo.save(sensor);
        }

        return "measurement added to sensor " + sensor.getName();
    }

    //http://localhost:8080/measurement/delete/
    @DeleteMapping("/delete/{measurementId}")
    public String delete(@PathVariable Long measurementId) {
        Measurement measurement = measurementRepo.findById(measurementId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid measurement ID"));

        measurementRepo.delete(measurement);

        return "measurement deleted";
    }

    @GetMapping("/getOnDemand")
    public String getOnDemandTemperature() {

        try (Socket socket = new Socket(espIp, espPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_TEMP");
            System.out.println("Sent GET_TEMP to ESP32");

            String response = in.readLine();
            if (response == null || response.isEmpty()) {
                return "No response received from ESP32.";
            }
            System.out.println("Response from ESP32: " + response);

            String[] parts = response.replace("{", "").replace("}", "").replace("\"", "").split(",");
            float temp = Float.parseFloat(parts[0].split(":")[1].trim());
            long sensorId = Long.parseLong(parts[1].split(":")[1].trim());

            Sensor sensor = sensorRepo.findById(sensorId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid sensor ID"));
            Measurement measurement = new Measurement(temp, sensor);
            sensor.addMeasurement(measurement);
            measurementRepo.save(measurement);

            if (!sensor.isStatus())
            {
                sensor.setStatus(true);
                sensorRepo.save(sensor);
            }

            return "Measurement added: " + response;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with ESP32: " + e.getMessage();
        }
    }
}
