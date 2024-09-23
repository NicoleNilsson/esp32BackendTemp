import flet as ft
import requests
import json
import os
from dotenv import load_dotenv

load_dotenv()
backend_url = os.getenv("BACKEND_URL")

if not backend_url:
    raise ValueError("BACKEND_URL environment variable is not set")

def get_on_demand_temperature(page):
    try:
        response = requests.get(backend_url)
        response.raise_for_status()
        
        data = response.text
        print("Response from server:", data)

        json_start_index = data.find("{")
        if json_start_index == -1:
            raise ValueError("Invalid response format: no JSON found")
        
        json_data = data[json_start_index:]
        json_obj = json.loads(json_data)
        temp = json_obj.get("temp")
        sensor_id = json_obj.get("sensorId")

        if temp is not None and sensor_id is not None:
            sensor_name = f"Sensor {sensor_id}"
            temperature = f"Temperature: {temp:.2f}°C"
            sensor_name_label.value = sensor_name
            temperature_label.value = temperature
        else:
            error_label.value = "Invalid data received"
        
        page.update()

    except (requests.exceptions.RequestException, ValueError) as e:
        error_label.value = f"Error fetching data: {e}"
        page.update()

def main(page: ft.Page):
    global sensor_name_label, temperature_label, error_label
    page.title = "Temperature Sensor"
    page.vertical_alignment = ft.MainAxisAlignment.CENTER
    
    sensor_name_label = ft.Text("Sensor: N/A", size=20)
    temperature_label = ft.Text("Temperature: N/A", size=20)
    error_label = ft.Text("", color=ft.colors.RED)
    
    get_button = ft.ElevatedButton(text="Get", on_click=lambda e: get_on_demand_temperature(page))
    
    page.add(sensor_name_label, temperature_label, get_button, error_label)

ft.app(target=main)