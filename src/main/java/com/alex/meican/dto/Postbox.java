package com.alex.meican.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/15 12:34 上午
 */

@Data
public class Postbox{
    private String postboxCode;
    private Long postboxOpenTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
