package com.example.esp32backendtemp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementDTO {
    private float temp;
    private Long sensorId;
}