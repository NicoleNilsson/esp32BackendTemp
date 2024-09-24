package com.example.esp32backendtemp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sensor", uniqueConstraints = {
        @UniqueConstraint(name = "UK_sensor_name", columnNames = "name")
})
@Data
@NoArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Measurement> measurements = new ArrayList<>();

    public Sensor(String name) {
        this.name = name;
    }

    public void addMeasurement(Measurement measurement) {
        measurement.setSensor(this);
        this.measurements.add(measurement);
    }
}

