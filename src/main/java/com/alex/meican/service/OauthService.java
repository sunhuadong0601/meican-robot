package com.alex.meican.service;

import com.alex.meican.constant.UrlConstant;
import com.alex.meican.dao.model.Member;
import com.alex.meican.exception.BusinessException;
import com.alex.meican.dto.response.TokenResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author sunhuadong
 * @date 2020/5/11 11:47 上午
 */
@Service
@Slf4j
public class OauthService {

    @Resource
    private OkHttpClient okHttpClient;
    @Value("${meican.oauth2.clientId}")
    private String clientId;
    @Value("${meican.oauth2.clientSecret}")
    private String clientSecret;

    public Member loginByUsernameAndPassword(String username, String password) {
        FormBody formBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "password")
                .add("meican_credential_type", "password")
                .add("username", username)
                .add("password", password)
                .add("username_type", "username")
                .build();
        Request request = new Request.Builder().url(UrlConstant.OAUTH_TOKEN_URL).post(formBody).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            Headers headers = response.headers();
            List<Cookie> cookieList = Cookie.parseAll(request.url(), headers);
            StringBuilder stringBuilder = new StringBuilder();
            if (!cookieList.isEmpty()) {
                for (Cookie cookie : cookieList) {
                    String name = cookie.name();
                    String value = cookie.value();
                    stringBuilder.append(name).append("=").append(value).append("; ");
                }
            }
            String cookies = stringBuilder.toString();
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                String body = responseBody.string();
                TokenResponse tokenResponse = JSON.parseObject(body, TokenResponse.class);
                String token = JSON.toJSONString(tokenResponse);
                return new Member().setUsername(username).setPassword(password).setCookies(cookies).setToken(token);
            }
        } catch (IOException e) {
            log.error("请求出错:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    public Member loginByCookies(String cookies) {
        if (cookies.contains("remember=")) {
            Request request = new Request.Builder().url(UrlConstant.ACCOUNTS_SHOW_URL).get().header("Cookie", cookies).build();
            try {
                ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
                if (Objects.nonNull(responseBody)) {
                    if (Objects.equals(responseBody.contentType(), MediaType.parse("application/json"))) {
                        // cookie请求成功
                        JSONObject jsonObject = JSONObject.parseObject(responseBody.string());
                        String email = jsonObject.getString("email");
                        return new Member().setUsername(email).setCookies(cookies);
                    }
                    throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
                }
            } catch (IOException e) {
                log.error("请求出错:{}", e.getMessage());
            }
            throw new BusinessException(BusinessException.BusinessStatus.networkException);
        }
        throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
    }
}
