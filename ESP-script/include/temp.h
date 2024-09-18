#pragma once

#include <Arduino.h>
#include <SimpleDHT.h>
#include <HTTPClient.h>

void setup();
void loop();
float getTemperature();
void jsonPayload(float temp, char* payload, size_t payloadSize);
bool sendTemperature(float temp);