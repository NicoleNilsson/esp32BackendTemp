package com.example.esp32backendtemp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Measurement {

    @Id
    @GeneratedValue
    private Long id;
    private float temp; //eller vill man ha double?
    private LocalDateTime measurementTime;

    @ManyToOne
    @JsonIgnore
    private Sensor sensor;

    public Measurement(float temp, Sensor sensor) {
        this.temp = temp;
        this.sensor = sensor;
        this.measurementTime = LocalDateTime.now(); //eller vill man skicka in tiden?
    }
}
