#include "temp.h"
#include "wifiSetup.h"
#include "serverCredentials.h"

#define DHTPIN 14
#define SENSOR_TYPE DHT11

DHT dht(DHTPIN, SENSOR_TYPE);
HTTPClient http;
const long SENSOR_ID = 1;

void setup() {
    setupSensor(dht);
    setupWifi();
}

void loop() {
    float t = getTemperature(dht);
    if (!isnan(t)) {
        sendTemperature(t);
    }
    delay(10000);
}

bool setupSensor(DHT &dht) {
    dht.begin();
    delay(2000);
    return true;
}

float getTemperature(DHT &dht) {
    return dht.readTemperature();
}

String jsonPayload(float temp) {
    char payload[50];
    snprintf(payload, sizeof(payload), "temp:%.2f:sensorid:%ld", temp, SENSOR_ID);
    return String(payload);
}

bool sendTemperature(float temp) {
    if (WiFi.status() != WL_CONNECTED) {
        return false;
    }

    http.begin(servername);
    http.addHeader("Content-Type", "text/plain");
    String jsonPayloadMessage = jsonPayload(temp);
    int httpResponseCode = http.POST(jsonPayloadMessage);

    http.end();
    return (httpResponseCode == HTTP_CODE_OK);
}