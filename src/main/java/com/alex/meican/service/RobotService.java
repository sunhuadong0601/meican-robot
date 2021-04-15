package com.alex.meican.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 机器人通知
 *
 * @author sunhuadong
 * @date 2020/8/20 10:54 上午
 */
@Service
public class RobotService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Value("${wechat.robot.key}")
    private String robotKey;

    @Resource
    private OkHttpClient okHttpClient;

    private static final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("robot-message-pool-%d").build();
    public static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());

    /**
     * 同步发送机器人消息
     *
     * @param text 内容
     */
    public void sendRobotMessage(String text) {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + robotKey;

        JSONObject content = new JSONObject();
        content.put("content", text);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", "text");
        jsonObject.put("text", content);

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(JSON, jsonObject.toJSONString()))
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步发送机器人消息
     *
     * @param text 内容
     */
    public void sendRobotMessageAsync(String text) {
        threadPoolExecutor.execute(() -> sendRobotMessage(text));
    }
}
