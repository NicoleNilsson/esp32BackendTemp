#pragma once

#include <Arduino.h>
#include <DHT.h>
#include <HTTPClient.h>

void setup();
void loop();
void printTemp(float t);
bool setupSensor(DHT &dht);
float getTemperature(DHT &dht);
String jsonPayload(float temp);
bool requestSent(int httpResponseCode);
bool sendTemperature(float temp);