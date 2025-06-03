package me.tjshawa.ttkdaaclient.utils;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AimbotDetector {
    private static final Gson gson = new Gson();

    private static class DetectionRequest {
        List<Double> data;
        String token; // 新增token字段

        DetectionRequest(List<Double> data, String token) {
            this.data = data;
            this.token = token; // 初始化token
        }
    }


    // 响应体数据结构
    public static class DetectionResponse {
        public String message;
        public double predicted;
    }

    public static DetectionResponse predict(List<Double> data, String token, final String SERVER_URL) {
        // 构建请求对象
        DetectionRequest request = new DetectionRequest(data, token);
        
        // 创建HTTP连接
        HttpURLConnection connection = null;
        try {
            URL url = new URL(SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            // 发送序列化请求
            try (OutputStream os = connection.getOutputStream();
                 OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                gson.toJson(request, osw);
            }

            // 处理响应
            int status = connection.getResponseCode();
            if (status == 200) {
                try (InputStream is = connection.getInputStream(); InputStreamReader isr = new InputStreamReader(is, "UTF-8")) {
                    return gson.fromJson(isr, DetectionResponse.class);
                }
            }
        } catch (Exception e) {

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}