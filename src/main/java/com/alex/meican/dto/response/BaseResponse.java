package com.alex.meican.dto.response;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/14 10:53 上午
 */
@Data
public class BaseResponse<T> {
    private String resultCode;
    private String resultDescription;
    private T data;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
