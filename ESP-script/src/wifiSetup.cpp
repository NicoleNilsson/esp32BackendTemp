#include "wifiSetup.h"
#include "wifiCredentials.h"

#define CONNECTING (WiFi.status() != WL_CONNECTED)
#define CONNECTION_TIMEOUT (millis() - connectionStart > connectionTimeout)
#define TOTAL_RETRIES 10

const unsigned long connectionTimeout = 20000;
int retries = 0;

bool setupWifi() {
    WiFi.begin(ssid, password);
    return hasConnection();
}

bool hasConnection() {
    unsigned long connectionStart = millis(); 
    while (CONNECTING && retries < TOTAL_RETRIES) {
        if (CONNECTION_TIMEOUT) {
            return false;
        }
        delay(1000);
        retries++;
    }
    return WiFi.status() == WL_CONNECTED;
}