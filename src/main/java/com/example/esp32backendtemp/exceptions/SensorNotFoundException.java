package com.example.esp32backendtemp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SensorNotFoundException extends RuntimeException {
    public SensorNotFoundException(String identifier, String type) {
        super("Sensor with " + type + " '" + identifier + "' not found.");
    }
}
