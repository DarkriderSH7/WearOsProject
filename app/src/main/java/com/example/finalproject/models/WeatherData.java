package com.example.finalproject.models;

import com.google.gson.annotations.SerializedName;

public class WeatherData {
    @SerializedName("name")
    private String cityName;

    @SerializedName("weather")
    private Weather[] weather;

    @SerializedName("main")
    private Main main;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("rain")
    private Precipitation rain;

    @SerializedName("snow")
    private Precipitation snow;

    public String getCityName() {
        return cityName;
    }

    public String getWeatherDescription() {
        return weather[0].getDescription();
    }

    public Main getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public Precipitation getRain() {
        return rain;
    }

    public Precipitation getSnow() {
        return snow;
    }

    public static class Weather {
        @SerializedName("description")
        private String description;

        public String getDescription() {
            return description;
        }
    }

    public static class Main {
        @SerializedName("temp")
        private double temperature;

        @SerializedName("pressure")
        private int pressure;

        @SerializedName("humidity")
        private int humidity;

        public double getTemperature() {
            return temperature;
        }

        public int getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;

        public double getSpeed() {
            return speed;
        }
    }

    public static class Precipitation {
        @SerializedName("1h")
        private double oneHour;

        public double getOneHour() {
            return oneHour;
        }
    }
}
