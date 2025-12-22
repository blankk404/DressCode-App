package com.dresscode.data.repository;

import com.dresscode.data.model.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 天气数据：使用 Open-Meteo（免 key）
 */
public class WeatherRepository {

    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface OnWeatherResult {
        void onSuccess(WeatherInfo info);
        void onFailed(String message);
    }

    public interface OnGeoResult {
        void onSuccess(String name, double lat, double lon);
        void onFailed(String message);
    }

    public void fetchCurrent(double lat, double lon, String city, OnWeatherResult cb) {
        executor.execute(() -> {
            try {
                String url = "https://api.open-meteo.com/v1/forecast"
                        + "?latitude=" + lat
                        + "&longitude=" + lon
                        + "&current=temperature_2m,weather_code,wind_speed_10m"
                        + "&timezone=auto";

                Request req = new Request.Builder().url(url).get().build();
                try (Response resp = client.newCall(req).execute()) {
                    if (!resp.isSuccessful()) {
                        cb.onFailed("天气请求失败：" + resp.code());
                        return;
                    }
                    String body = resp.body() != null ? resp.body().string() : "";
                    JSONObject json = new JSONObject(body);
                    JSONObject current = json.getJSONObject("current");
                    WeatherInfo info = new WeatherInfo();
                    info.city = city;
                    info.latitude = lat;
                    info.longitude = lon;
                    info.temperatureC = current.optDouble("temperature_2m", Double.NaN);
                    info.weatherCode = current.optInt("weather_code", -1);
                    info.windSpeedKmh = current.optDouble("wind_speed_10m", Double.NaN);
                    info.weatherText = weatherCodeToText(info.weatherCode);
                    cb.onSuccess(info);
                }
            } catch (Exception e) {
                cb.onFailed("天气获取失败：" + e.getMessage());
            }
        });
    }

    public void geocodeCity(String cityName, OnGeoResult cb) {
        executor.execute(() -> {
            try {
                String name = URLEncoder.encode(cityName, StandardCharsets.UTF_8.name());
                String url = "https://geocoding-api.open-meteo.com/v1/search"
                        + "?name=" + name
                        + "&count=1&language=zh&format=json";
                Request req = new Request.Builder().url(url).get().build();
                try (Response resp = client.newCall(req).execute()) {
                    if (!resp.isSuccessful()) {
                        cb.onFailed("城市解析失败：" + resp.code());
                        return;
                    }
                    String body = resp.body() != null ? resp.body().string() : "";
                    JSONObject json = new JSONObject(body);
                    JSONArray results = json.optJSONArray("results");
                    if (results == null || results.length() == 0) {
                        cb.onFailed("未找到该城市");
                        return;
                    }
                    JSONObject r = results.getJSONObject(0);
                    double lat = r.getDouble("latitude");
                    double lon = r.getDouble("longitude");
                    String n = r.optString("name", cityName);
                    String admin1 = r.optString("admin1", "");
                    String country = r.optString("country", "");
                    String display = n;
                    if (!admin1.isEmpty() && !display.contains(admin1)) display = display + "·" + admin1;
                    if (!country.isEmpty() && !display.contains(country)) display = display + "·" + country;
                    cb.onSuccess(display, lat, lon);
                }
            } catch (Exception e) {
                cb.onFailed("城市解析失败：" + e.getMessage());
            }
        });
    }

    public static String weatherCodeToText(int code) {
        // Open-Meteo WMO Weather interpretation codes
        switch (code) {
            case 0: return "晴";
            case 1: return "大致晴";
            case 2: return "多云";
            case 3: return "阴";
            case 45:
            case 48: return "雾";
            case 51:
            case 53:
            case 55: return "毛毛雨";
            case 56:
            case 57: return "冻毛毛雨";
            case 61:
            case 63:
            case 65: return "雨";
            case 66:
            case 67: return "冻雨";
            case 71:
            case 73:
            case 75: return "雪";
            case 77: return "冰粒";
            case 80:
            case 81:
            case 82: return "阵雨";
            case 85:
            case 86: return "阵雪";
            case 95: return "雷暴";
            case 96:
            case 99: return "雷暴伴冰雹";
            default: return "未知天气";
        }
    }
}


