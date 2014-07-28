package com.example.jarp.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by JARP on 18/07/2014.
 */
public class JsonParser {

    JSONObject reader;

    JSONArray list ;

    private final String LIST_TAG="list";

    ArrayList<DayForecast> listForecast ;

    public void parse(String in, int index ) throws JSONException {

        reader = new JSONObject(in);

        list = reader.getJSONArray("list");

        listForecast = new ArrayList<DayForecast>();
        DayForecast d;

        for (int i = 0; i < list.length(); i++) {
            d = getForecast(list.getJSONObject(i));
            listForecast.add(d);
        }

        listForecast.get(index).temperature.getMax();

    }

    private DayForecast getForecast (JSONObject forecastDay) throws JSONException {
        DayForecast d = new DayForecast();
        d.date = getDate(forecastDay.getString(DayForecast.DATE_TAG));
        d.pressure = getDouble(forecastDay.getString(DayForecast.PRESSURE_TAG));
        d.humidity = getDouble(forecastDay.getString(DayForecast.HUMIDITY_TAG));
        d.temperature =getTemperature(forecastDay.getJSONObject(DayForecast.TEMPERATURE_TAG))  ;
        d.weather = getWeathers(forecastDay.getJSONArray(DayForecast.WEATHER_TAG))  ;
        d.speed = getDouble(forecastDay.getString(DayForecast.SPEED_TAG));
        d.deg =getDouble(forecastDay.getString(DayForecast.DEG_TAG));
        d.clouds = getDouble(forecastDay.getString(DayForecast.CLOUDS_TAG));

        return  d;

    }

    private Date getDate(String unixTime)
    {
        return new java.util.Date((long)getLong(unixTime)*1000);
    }

    private Double getDouble(String doubl)
    {
        return  Double.parseDouble(doubl);
    }

    private ArrayList<Weather> getWeathers(JSONArray weatherDay) throws JSONException {

        ArrayList<Weather> weathers = new ArrayList<Weather>();
        if(weatherDay!=null)
        for (int i = 0; i <weatherDay.length() ; i++) {
            weathers.add(getWeather(weatherDay.getJSONObject(i)));
        }
        return weathers;

    }

    private Weather getWeather(JSONObject weatherDay) throws JSONException {
        Weather w = new Weather();
        w.id = Integer.parseInt(weatherDay.getString(Weather.ID_TAG));
        w.main = weatherDay.getString(Weather.MAIN_TAG);
        w.description= weatherDay.getString(Weather.DESCRIPTION_TAG);;
        w.icon= weatherDay.getString(Weather.ICON_TAG);
        return  w;
    }

    private Temperature getTemperature (JSONObject temperatureDay) throws JSONException {
        Temperature t = new Temperature();
        t.day = getDouble(temperatureDay.getString(Temperature.DAY_TAG));
        t.min = getDouble(temperatureDay.getString(Temperature.MIN_TAG));
        t.max = getDouble(temperatureDay.getString(Temperature.MAX_TAG));
        t.night = getDouble(temperatureDay.getString(Temperature.NIGHT_TAG));
        t.eve = getDouble(temperatureDay.getString(Temperature.EVE_TAG));
        t.morn = getDouble(temperatureDay.getString(Temperature.MORN_TAG));
        return t;
    }

    private class DayForecast {

        public static final String DATE_TAG="dt";
        public static final String PRESSURE_TAG="pressure";
        public static final String HUMIDITY_TAG="humidity";
        public static final String TEMPERATURE_TAG="temp";
        public static final String WEATHER_TAG="weather";
        public static final String SPEED_TAG="speed";
        public static final String DEG_TAG="deg";
        public static final String CLOUDS_TAG="clouds";


        Date date ;
        Double pressure;
        Double humidity;
        Temperature temperature;
        ArrayList<Weather> weather;
        Double speed;
        Double deg;
        Double clouds;

    }

    private class Temperature
    {
        public static final String DAY_TAG="day";
        public static final String MIN_TAG="min";
        public static final String MAX_TAG="max";
        public static final String NIGHT_TAG="night";
        public static final String EVE_TAG="eve";
        public static final String MORN_TAG="morn";

        private Double day;
        private Double min;
        private Double max;
        private Double night;
        private Double eve;
        private Double morn;

        public Double getDay() {
            return day;
        }

        public void setDay(Double day) {
            this.day = day;
        }

        public Double getMin() {
            return min;
        }

        public void setMin(Double min) {
            this.min = min;
        }

        public Double getMax() {
            return max;
        }

        public void setMax(Double max) {
            this.max = max;
        }

        public Double getNight() {
            return night;
        }

        public void setNight(Double night) {
            this.night = night;
        }

        public Double getEve() {
            return eve;
        }

        public void setEve(Double eve) {
            this.eve = eve;
        }

        public Double getMorn() {
            return morn;
        }

        public void setMorn(Double morn) {
            this.morn = morn;
        }
    }

    private class Weather {


        public static final String ID_TAG="id";
        public static final String MAIN_TAG="main";
        public static final String DESCRIPTION_TAG="description";
        public static final String ICON_TAG="icon";

         private int id;
         private String main;
         private String description;
         private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }


    private static long getLong(String unixTime)
    {
        return Long.parseLong(unixTime);
    }

}
