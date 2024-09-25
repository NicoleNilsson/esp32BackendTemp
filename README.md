# Temperature Monitoring System

This project is an ESP32-based temperature monitoring system that collects temperature data from a DHT11 sensor and communicates the data to a backend system. The backend is built using Spring Boot and communicates with the ESP32 device via TCP/IP to receive on-demand temperature readings and store them in a database. Additionally, the system allows users to retrieve and manage measurements and sensor information via REST APIs.

## Features

1.	ESP32 Integration:

   •	The ESP32 collects temperature data using the DHT11 sensor.

   •	The ESP32 handles client requests via TCP, sending the current temperature when requested.

   •	Automatic temperature readings are sent periodically to the backend server.

   •	Supports Wi-Fi connection and reconnection logic.

2.	Spring Boot Backend:
	    
    •	RESTful API for interacting with the temperature data and sensors.

	•	Fetch temperature measurements for specific sensors and dates.

	•	Add, delete, and manage sensors and measurements.

	•	On-demand temperature reading from the ESP32 via TCP.

	•	Exception handling for sensor not found and communication errors.

3.	Database Integration:

	•	The system stores temperature measurements and sensor information in a relational database.

    •	Measurements are linked to sensors for easy retrieval and management.


## How it Works

ESP32 Side

  •	The ESP32 establishes a Wi-Fi connection using credentials provided in wifiSetup.h.

  •	The TCP server on the ESP32 listens for incoming client connections.

  •	When a client sends a GET_TEMP request, the ESP32 reads the temperature from the DHT11 sensor and responds with a JSON object containing the temperature and the sensor ID.

  •	The ESP32 periodically sends temperature data to the backend server using HTTP POST requests.

Backend Side

  •	The Spring Boot backend serves as the data manager for sensors and measurements.

  •	When the /measurement/getOnDemand endpoint is hit, the backend establishes a TCP connection with the ESP32 to get the current temperature.

  •	All measurements are saved in the database, and users can retrieve them via REST APIs.


## API Documentation

The backend provides several REST endpoints for interacting with sensors and measurements:

***Measurement Endpoints***

  •	GET /measurement/getall: Returns all measurements.

  •	GET /measurement/getbydate/{date}: Returns measurements by date.

  •	POST /measurement/add: Adds a new measurement.

  •	GET /measurement/getOnDemand: Fetches the current temperature from the ESP32.

***Sensor Endpoints***

  •	GET /sensor/getall: Returns all sensors.

  •	GET /sensor/getbyid/{sensorId}: Returns a sensor by ID.

  •	POST /sensor/add: Adds a new sensor.

  •	DELETE /sensor/delete/{sensorId}: Deletes a sensor.

  •	GET /sensor/getbyname/{name}/measurements/{date}: Retrieves measurements for a sensor by name and date.


## Setup and Installation

### 1. ***ESP32***
   
  •	Install Platformio Extension

  •	Choose the esp32 as the board

  •	Required Libraries: SimpleDHT11, WiFiClient, HTTPClient


### 2. ***Backend***

  •	Install Java 17+ or later.

  •	Install Spring boot

  •	Setup a relational database (eg. MySQL, PostgreSQL)

  •	Configure the database connection in the application.properties file.


### 3. ***ESP32 Setup***

 1.	Connect the DHT11 sensor to the ESP32 (DHTPIN is set to GPIO 14 by default).

 2.	Flash the ESP32 code to the device using Platform io.

 3.	Ensure that Wi-Fi credentials are correctly set in wifiCredentials.h.

 4.	Set the server IP and port in serverCredentials.h.


### 4. ***Backend Setup***

 1.	Clone or download this repository.

 2.	Configure the database connection settings in the application.properties file.

 3.	Build and run the Spring Boot application:

```bash
    mvn clean install
    mvn spring-boot:run
```

 4. The backend server will be running on http://localhost:8080.

### Running the System

 1.	Power on the ESP32 and ensure it’s connected to the Wi-Fi network.

 2.	Access the backend REST API via http://localhost:8080 to add sensors, get measurements, or request on-demand temperature readings from the ESP32.


