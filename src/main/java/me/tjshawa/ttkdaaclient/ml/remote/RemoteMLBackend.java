package me.tjshawa.ttkdaaclient.ml.remote;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import me.tjshawa.ttkdaaclient.ml.AimbotResponse;
import me.tjshawa.ttkdaaclient.ml.MLBackend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@AllArgsConstructor
public class RemoteMLBackend implements MLBackend {
    @NonNull @NotNull
    private final String endpoint;
    @NonNull @NotNull
    private final String token;

    private static final Gson gson = new Gson();

    @AllArgsConstructor
    @Getter
    private static class DetectionRequest {
        List<Double> data;
        String token;
    }

    @Override
    @Nullable
    public AimbotResponse predictAimML(List<Double> data) {
        // 构建请求对象
        DetectionRequest request = new DetectionRequest(data, token);

        // 创建HTTP连接
        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            // 发送序列化请求
            try (OutputStream os = connection.getOutputStream();
                 OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8")) {
                gson.toJson(request, osw);
            }

            // 处理响应
            int status = connection.getResponseCode();
            if (status == 200) {
                try (InputStream is = connection.getInputStream(); InputStreamReader isr = new InputStreamReader(is, "UTF-8")) {
                   AimbotResponse response = gson.fromJson(isr, AimbotResponse.class);
                    return response;
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
