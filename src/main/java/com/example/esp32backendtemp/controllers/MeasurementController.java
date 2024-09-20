package com.example.esp32backendtemp.controllers;

import com.example.esp32backendtemp.DTOs.MeasurementDTO;
import com.example.esp32backendtemp.models.Measurement;
import com.example.esp32backendtemp.repositories.MeasurementRepo;
import com.example.esp32backendtemp.models.Sensor;
import com.example.esp32backendtemp.repositories.SensorRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    //http://localhost:8080/measurement/add
    //temp = 23, sensorid = 1
    @PostMapping("/add")
    public String add(@RequestBody MeasurementDTO data) {
        Sensor sensor = sensorRepo.findById(data.getSensorId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid sensor ID"));

        Measurement measurement = new Measurement(data.getTemp(), sensor);

        sensor.addMeasurement(measurement);
        measurementRepo.save(measurement);

        return "measurement added to sensor " + sensor.getName();
    }

    //http://localhost:8080/measurement/delete/
    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Measurement measurement = measurementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sensor ID"));

        measurementRepo.delete(measurement);

        return "measurement deleted";
    }

    @GetMapping("/getOnDemand")
    public String getOnDemandTemperature() {

        try (Socket socket = new Socket(espIp, espPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("GET_TEMP");

            String response = in.readLine();
            System.out.println("Response from ESP32: " + response);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error communicating with ESP32: " + e.getMessage();
        }
    }
}
