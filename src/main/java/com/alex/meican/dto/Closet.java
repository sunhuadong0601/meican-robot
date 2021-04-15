package com.alex.meican.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/15 12:52 上午
 */
@Data
public class Closet {
    private int closetId;
    private String closetCode;
    private String closetVersion;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
