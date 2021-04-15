package com.alex.meican.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:20 上午
 */
@Data
public class Calendar {
    private Long targetTime;
    private String title;
    private UserTab userTab;
    private OpeningTime openingTime;
    private Order corpOrderUser;
    private String status;
    private String reason;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}