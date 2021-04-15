package com.alex.meican.dto;

import com.alex.meican.dao.model.Corp;
import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:20 上午
 */
@Data
public class OpeningTime {
    private String uniqueId;
    private String name;
    private String openTime;
    private String closeTime;
    private String defaultAlarmTime;
    private String postboxOpenTime;
    private Corp corp;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}