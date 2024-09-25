package com.example.esp32backendtemp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float temp;
    private LocalDateTime measurementTime;

    @ManyToOne
    @JsonIgnore
    private Sensor sensor;

    public Measurement(float temp, Sensor sensor) {
        this.temp = temp;
        this.sensor = sensor;
        this.measurementTime = LocalDateTime.now();
    }
}
