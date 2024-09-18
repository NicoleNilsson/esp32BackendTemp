#pragma once

#include <Arduino.h>
#include <DHT.h>
#include <HTTPClient.h>

void setup();
void loop();
bool setupSensor(DHT &dht);
float getTemperature(DHT &dht);
String jsonPayload(float temp);
bool sendTemperature(float temp);