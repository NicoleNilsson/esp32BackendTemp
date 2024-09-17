#include "wifiSetup.h"
#include "wifiCredentials.h"

#define CONNECTING (WiFi.status() != WL_CONNECTED)
#define CONNECTION_TIMEOUT (millis() - connectionStart > connectionTimeout)
#define TOTAL_RETRIES 10

const unsigned long connectionTimeout = 20000;
int retries = 0;

void setupWifi() {
    WiFi.begin(ssid, password);
    if (!hasConnection()) {
        Serial.println("Failed to connect to WiFi");
    } else {
        Serial.println("Connected to WiFi");
        Serial.print("ESP32 IP Address: ");
        Serial.println(WiFi.localIP());
    }
}

bool hasConnection() {
    unsigned long connectionStart = millis(); 
    while (CONNECTING && retries < TOTAL_RETRIES) {
        if (CONNECTION_TIMEOUT) {
            Serial.println("Connection timed out.");
            return false;
        }
        delay(1000);
        Serial.print("Connecting to WiFi.. Status: ");
        Serial.println(WiFi.status());
        retries++;
    }
    return WiFi.status() == WL_CONNECTED;
}