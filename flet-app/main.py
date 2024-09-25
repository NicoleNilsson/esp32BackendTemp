import flet as ft
import requests
import json
import os
import asyncio
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

async def main(page: ft.Page):
    global sensor_name_label, temperature_label, error_label, content_column
    
    page.title = "Temperature Sensor"
    page.vertical_alignment = ft.MainAxisAlignment.CENTER

    welcome_text = ft.Text("Welcome to the Temperature Sensor App!", size=24, weight="bold")
    
    sensor_name_label = ft.Text("Sensor: N/A", size=20, opacity=0)
    temperature_label = ft.Text("Temperature: N/A", size=20, opacity=0)
    error_label = ft.Text("", color=ft.colors.RED, opacity=0)
    
    get_button = ft.ElevatedButton(
        text="Get", 
        opacity=0, 
        on_click=lambda e: get_on_demand_temperature(page)
    )

    content_column = ft.AnimatedSwitcher(
        content=ft.Column(
            [
                sensor_name_label,
                temperature_label,
                get_button,
                error_label
            ],
            alignment=ft.MainAxisAlignment.CENTER,
            horizontal_alignment=ft.CrossAxisAlignment.CENTER,
        ),
        duration=500,
    )
    
    page.add(welcome_text)
    
    async def show_main_content():
        await asyncio.sleep(2)
        
        welcome_text.opacity = 0
        welcome_text.update()

        await asyncio.sleep(1)

        sensor_name_label.opacity = 1
        temperature_label.opacity = 1
        get_button.opacity = 1
        error_label.opacity = 1

        content_column.content = ft.Column(
            [
                sensor_name_label,
                temperature_label,
                get_button,
                error_label
            ],
            alignment=ft.MainAxisAlignment.CENTER,
            horizontal_alignment=ft.CrossAxisAlignment.CENTER,
        )
        page.update()

    page.add(ft.ProgressBar(width=200))
    page.update()

    await show_main_content()

ft.app(target=main)