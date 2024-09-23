package com.example.esp32backendtemp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sensor_id")
    private List<Measurement> measurements = new ArrayList<>();

    public Sensor(String name) {
        this.name = name;
    }

    public void addMeasurement(Measurement measurement) {
        measurement.setSensor(this);
        this.measurements.add(measurement);
    }
}
