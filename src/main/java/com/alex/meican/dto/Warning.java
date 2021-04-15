package com.alex.meican.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;
/**
 * @author sunhuadong
 * @date 2020/5/14 1:20 上午
 */
@Data
public class Warning {
    private String backgroundColorCode;
    private String textColorCode;
    private String text;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}