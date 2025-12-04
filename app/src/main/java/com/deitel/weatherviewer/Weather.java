package com.deitel.weatherviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Weather {
    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String icon; // emoji do JSON

    // Construtor usando data formatada "yyyy-MM-dd"
    public Weather(String dateString, double minTempC, double maxTempC,
                   double humidityFraction, String description, String icon) {

        this.dayOfWeek = convertDateToDayName(dateString);

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);

        this.minTemp = nf.format(minTempC) + "°C";
        this.maxTemp = nf.format(maxTempC) + "°C";

        this.humidity = NumberFormat.getPercentInstance().format(humidityFraction);
        this.description = description;
        this.icon = icon; // emoji (ex: "☀️", "⛅", "🌧️")
    }

    // Converte data "2025-11-26" → "Wednesday" ou "Quarta-feira"
    private String convertDateToDayName(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date d = sdf.parse(dateString);
            SimpleDateFormat out = new SimpleDateFormat("EEEE", Locale.getDefault());
            return out.format(d);
        } catch (Exception e) {
            return dateString; // fallback
        }
    }
}
