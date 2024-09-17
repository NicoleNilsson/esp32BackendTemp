#include "temp.h"
#include "wifiSetup.h"
#include "serverCredentials.h"

#define DHTPIN 14
#define SENSOR_TYPE DHT11
#define READING (!isnan(temp))
#define HAS_CONNECTION (WiFi.status() == WL_CONNECTED)
#define REQUEST_SENT (httpResponseCode > 0)

const long SENSOR_ID = 1;

DHT dht(DHTPIN, SENSOR_TYPE);
HTTPClient http;

void setup() {
  Serial.begin(115200);
  Serial.println("DHT Sensor Test");
  dht.begin();
  setupWifi();
}

void loop() {
  float t = getTemperature(dht);
  printTemp(t);

  if (HAS_CONNECTION) {
    sendTemperature(t);
  } else {
    Serial.println("No connection to server.");
  }
  delay(10000);
}

void printTemp(float t) {
    Serial.print("Temperature: ");
    Serial.print(t);
    Serial.println("°C");
}

bool setupSensor(DHT &dht) {
    dht.begin();
    delay(2000);
    return true;
}

float getTemperature(DHT &dht) {
    float temp = dht.readTemperature();
    if (isnan(temp)) {
        Serial.println("Failed to read temperature from DHT sensor.");
    }
    return temp;
}

String jsonPayload(float temp) {
    return "temp:" + String(temp) + ":sensorid:" + String(SENSOR_ID);
}


bool requestSent(int httpResponseCode) {
    if (!REQUEST_SENT)
        return false;
    String response = http.getString();
    Serial.println(httpResponseCode);
    Serial.println(response);
    return true;
}

bool sendTemperature(float temp) {
    if (!HAS_CONNECTION) {
        Serial.println("No WiFi connection.");
        return false;
    }

    http.begin(servername);
    http.addHeader("Content-Type", "application/json");

    String jsonPayloadMessage = jsonPayload(temp);
    Serial.print("Sending POST request to ");
    Serial.println(servername);
    Serial.print("Payload: ");
    Serial.println(jsonPayloadMessage);

    int httpResponseCode = http.POST(jsonPayloadMessage);

    if (httpResponseCode > 0) {
        Serial.print("HTTP Response code: ");
        Serial.println(httpResponseCode);
        String response = http.getString();
        Serial.println("Response: ");
        Serial.println(response);
    } else {
        Serial.print("Error on sending POST: ");
        Serial.println(httpResponseCode);
    }

    http.end();
    return httpResponseCode > 0;
}