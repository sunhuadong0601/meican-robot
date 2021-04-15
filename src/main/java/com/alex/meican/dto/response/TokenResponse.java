package com.alex.meican.dto.response;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/11 12:52 下午
 */
@Data
public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private long expiresIn;
    private boolean needResetPassword;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
