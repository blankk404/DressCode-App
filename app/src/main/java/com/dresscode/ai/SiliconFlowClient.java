package com.dresscode.ai;

import com.dresscode.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SiliconFlowClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public List<String> fetchXhsImageUrls(String query) throws Exception {
        if (BuildConfig.SILICONFLOW_API_KEY == null || BuildConfig.SILICONFLOW_API_KEY.isEmpty()) {
            throw new IllegalStateException("SILICONFLOW_API_KEY 未配置");
        }

        JSONObject body = new JSONObject();
        body.put("model", BuildConfig.SILICONFLOW_MODEL_NAME);
        body.put("max_tokens", BuildConfig.SILICONFLOW_MAX_TOKENS);
        body.put("temperature", BuildConfig.SILICONFLOW_TEMPERATURE);

        JSONArray messages = new JSONArray();
        JSONObject sys = new JSONObject();
        sys.put("role", "system");
        sys.put("content", "你是一个信息提取助手。只输出严格的 JSON。");
        messages.put(sys);

        JSONObject user = new JSONObject();
        user.put("role", "user");
        user.put("content",
                "请基于小红书平台，针对关键词【" + query + "】给出10个可直接访问的图片URL，" +
                        "只输出JSON数组（例如：[\"https://...\",\"https://...\"]），不要输出其他任何文字。");
        messages.put(user);

        body.put("messages", messages);

        Request request = new Request.Builder()
                .url(BuildConfig.SILICONFLOW_API_BASE + "/chat/completions")
                .addHeader("Authorization", "Bearer " + BuildConfig.SILICONFLOW_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body.toString(), JSON))
                .build();

        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new RuntimeException("请求失败: " + resp.code());
            }
            String s = resp.body() != null ? resp.body().string() : "";
            JSONObject json = new JSONObject(s);
            JSONArray choices = json.optJSONArray("choices");
            if (choices == null || choices.length() == 0)
                return new ArrayList<>();
            JSONObject msg = choices.getJSONObject(0).getJSONObject("message");
            String content = msg.optString("content", "");

            // 解析模型输出的 JSON 数组
            JSONArray arr = new JSONArray(content.trim());
            List<String> urls = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                String url = arr.optString(i, "");
                if (url != null && !url.isEmpty())
                    urls.add(url);
            }
            return urls;
        }
    }
}
