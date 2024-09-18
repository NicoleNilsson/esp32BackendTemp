package com.example.esp32backendtemp.controllers;

import com.example.esp32backendtemp.models.Sensor;
import com.example.esp32backendtemp.repositories.SensorRepo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    private final SensorRepo sensorRepo;

    public SensorController(SensorRepo sensorRepo) {
        this.sensorRepo = sensorRepo;
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
    public List<Sensor> getByName(@PathVariable String name) {
        return sensorRepo.findByName(name);
    }

    //http://localhost:8080/sensor/add?name=
    @RequestMapping("/add")
    public String add(@RequestParam String name){
        sensorRepo.save(new Sensor(name));
        return "sensor " + name + " added";
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
