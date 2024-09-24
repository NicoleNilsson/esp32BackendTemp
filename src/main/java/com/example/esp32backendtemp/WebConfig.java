package com.example.esp32backendtemp;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000", // Windows frontend origin
                        "http://localhost:5173", // Mac frontend origin (e.g., if using Vite)
                        "http://127.0.0.1:5173"  // Additional local origins if needed
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow all HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // Allow credentials if needed
                .maxAge(3600);
    }
}
