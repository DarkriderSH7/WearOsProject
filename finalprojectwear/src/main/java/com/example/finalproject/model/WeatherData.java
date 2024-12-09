package com.example.finalproject.model;

// WeatherData.java
public class WeatherData {
    private String title;
    private String value;

    public WeatherData(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public static WeatherData fromString(String data) {
        String[] parts = data.split(":");
        return new WeatherData(parts[0].trim(), parts[1].trim());
    }
}
