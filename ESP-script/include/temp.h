#pragma once

#include <Arduino.h>
#include <SimpleDHT.h>
#include <HTTPClient.h>
#include <WiFi.h>

void setup();
void loop();
void checkForClientRequests();
void checkForAutomaticTemperatureSend();
void processClientRequest(WiFiClient& client, String& currentLine);
float getTemperature();
void jsonPayload(float temp, char* payload, size_t payloadSize);
bool sendTemperature(float temp);