#include "temp.h"
#include "wifiSetup.h"
#include "serverCredentials.h"

#define DHTPIN 14
#define SENSOR_TYPE SimpleDHT11
#define SERVER_PORT 12345

SimpleDHT11 dht;
HTTPClient http;
WiFiServer tcpServer(SERVER_PORT);
const long SENSOR_ID = 152;

void setup() {
    Serial.begin(115200);
    delay(2000);

    bool wifiConnected = setupWifi();
    if (wifiConnected) {
        Serial.print("ESP32 connected with IP: ");
        Serial.println(WiFi.localIP());
        tcpServer.begin();
        Serial.println("TCP server started");
    } else {
        Serial.println("WiFi connection failed");
    }
}

void loop() {
    WiFiClient client = tcpServer.available();

    if (client) {
        Serial.println("New Client Connected");
        String currentLine = "";
        while (client.connected()) {
            if (client.available()) {
                char c = client.read();
                if (c == '\n') {
                    Serial.println("Received request: " + currentLine);
                    
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
                    currentLine = "";
                    break;
                } else if (c != '\r') {
                    currentLine += c;
                }
            }
        }
        client.stop();
        Serial.println("Client Disconnected");
    }
}

float getTemperature() {
    byte temperature = 0;
    uint8_t err = dht.read(DHTPIN, &temperature, NULL, NULL);
    if (err != SimpleDHTErrSuccess) {
        Serial.print("DHT11 Read Error: ");
        Serial.println(err);
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

    http.end();
    return (httpResponseCode == HTTP_CODE_OK);
}