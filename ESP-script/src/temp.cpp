#include "temp.h"
#include "wifiSetup.h"
#include "serverCredentials.h"

#define DHTPIN 14
#define SENSOR_TYPE SimpleDHT11
#define SERVER_PORT 12345
#define TIMEOUT_INTERVAL 60000

SimpleDHT11 dht;
HTTPClient http;
WiFiServer tcpServer(SERVER_PORT);
const long SENSOR_ID = 202;

unsigned long lastPingTime = 0;
bool pingReceived = false;

void setup() {
    Serial.begin(115200);
    delay(2000);
    if (setupWifi()) {
        tcpServer.begin();
    }
}

void loop() {
    checkForClientRequests();
    checkForAutomaticTemperatureSend();
}

void checkForClientRequests() {
    WiFiClient client = tcpServer.available();
    if (client) {
        String currentLine = "";
        while (client.connected()) {
            if (client.available()) {
                char c = client.read();
                if (c == '\n') {
                    processClientRequest(client, currentLine);
                    break;
                } else if (c != '\r') {
                    currentLine += c;
                }
            }
        }
        client.flush();
        client.stop();
    }
}

void processClientRequest(WiFiClient& client, String& currentLine) {
    lastPingTime = millis();
    pingReceived = true;
    
    if (currentLine.startsWith("GET_TEMP")) {
        float temperature = getTemperature();
        if (isnan(temperature)) {
            client.println("ERROR: Failed to read temperature");
        } else {
            char response[64];
            jsonPayload(temperature, response, sizeof(response));
            client.println(response);
        }
    } else {
        client.println("ERROR: Unknown Command");
    }
}

void checkForAutomaticTemperatureSend() {
    if (millis() - lastPingTime >= TIMEOUT_INTERVAL) {
        float temp = getTemperature();
        if (!isnan(temp)) {
            Serial.print("Read temp: ");
            Serial.println(temp);
            if (sendTemperature(temp)) {
                Serial.println("temp sent");
            } else {
                Serial.println("temp failed to send");
            }
        } else {
            Serial.println("Failed to read temp");
        }
        lastPingTime = millis();
    }
}

float getTemperature() {
    byte temperature = 0;
    uint8_t err = dht.read(DHTPIN, &temperature, NULL, NULL);
    if (err != SimpleDHTErrSuccess) {
        delay(2000);
        return NAN;
    }
    return static_cast<float>(temperature);
}

void jsonPayload(float temp, char* payload, size_t payloadSize) {
    snprintf(payload, payloadSize, "{\"temp\": %.2f, \"sensorId\": %u}", temp, SENSOR_ID);
}

bool sendTemperature(float temp) {
    if (WiFi.status() != WL_CONNECTED) {
        return false;
    }
    http.begin(servername);
    http.addHeader("Content-Type", "application/json");

    char payload[64];
    jsonPayload(temp, payload, sizeof(payload));

    int httpResponseCode = http.POST((uint8_t*)payload, strlen(payload));
    if (httpResponseCode != HTTP_CODE_OK) {
        http.end();
        return false;
    }
    http.end();
    return true;
}