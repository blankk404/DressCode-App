package com.dresscode.ai;

import com.dresscode.BuildConfig;
import com.dresscode.data.model.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WeatherOutfitAdvisorClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public String suggest(WeatherInfo w) throws Exception {
        if (BuildConfig.SILICONFLOW_API_KEY == null || BuildConfig.SILICONFLOW_API_KEY.isEmpty()) {
            throw new IllegalStateException("SILICONFLOW_API_KEY 未配置");
        }

        JSONObject body = new JSONObject();
        body.put("model", BuildConfig.SILICONFLOW_MODEL_NAME);
        body.put("max_tokens", 200);
        body.put("temperature", 0);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "你是穿搭助手。输出简短可执行建议，控制在60字以内，不要列清单。"));

        String prompt = "今日天气信息：城市=" + safe(w.city)
                + "，天气=" + safe(w.weatherText)
                + "，气温=" + formatTemp(w.temperatureC) + "°C"
                + "，风速=" + formatNum(w.windSpeedKmh) + "km/h。"
                + "请给出今日穿着建议（上衣/外套/裤装/鞋子方向即可），口吻自然。";

        messages.put(new JSONObject().put("role", "user").put("content", prompt));
        body.put("messages", messages);

        Request request = new Request.Builder()
                .url(BuildConfig.SILICONFLOW_API_BASE + "/chat/completions")
                .addHeader("Authorization", "Bearer " + BuildConfig.SILICONFLOW_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), JSON))
                .build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) throw new RuntimeException("建议请求失败: " + resp.code());
            String s = resp.body() != null ? resp.body().string() : "";
            JSONObject json = new JSONObject(s);
            JSONArray choices = json.optJSONArray("choices");
            if (choices == null || choices.length() == 0) return "暂无建议";
            JSONObject msg = choices.getJSONObject(0).getJSONObject("message");
            return msg.optString("content", "暂无建议").trim();
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String formatTemp(double d) {
        if (Double.isNaN(d)) return "--";
        return String.valueOf(Math.round(d));
    }

    private static String formatNum(double d) {
        if (Double.isNaN(d)) return "--";
        return String.valueOf(Math.round(d));
    }
}


