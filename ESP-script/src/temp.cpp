#include "temp.h"
#include "wifiSetup.h"
#include "serverCredentials.h"

#define DHTPIN 14
#define SENSOR_TYPE SimpleDHT11

SimpleDHT11 dht;
HTTPClient http;
const long SENSOR_ID = 1;

void setup() {
    delay(2000);
    bool wifiConnected = setupWifi();
}

void loop() {
    float t = getTemperature();
    if (!isnan(t)) {
        sendTemperature(t);
    }
    delay(10000);
}

float getTemperature() {
    byte temperature = 0;
    uint8_t err = dht.read(DHTPIN, &temperature, NULL, NULL);
    if (err != SimpleDHTErrSuccess) {
        return NAN;
    }
    return static_cast<float>(temperature);
}

void jsonPayload(float temp, char* payload, size_t payloadSize) {
    snprintf(payload, payloadSize, "temp:%.2f:sensorid:%ld", temp, SENSOR_ID);
}

bool sendTemperature(float temp) {
    if (WiFi.status() != WL_CONNECTED) {
        return false;
    }

    http.begin(servername);
    http.addHeader("Content-Type", "text/plain");
    char payload[32];
    jsonPayload(temp, payload, sizeof(payload));
    int httpResponseCode = http.POST((uint8_t*)payload, strlen(payload));

    http.end();
    return (httpResponseCode == HTTP_CODE_OK);
}