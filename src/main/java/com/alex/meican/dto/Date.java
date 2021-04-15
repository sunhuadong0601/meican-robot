package com.alex.meican.dto;


import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:20 上午
 */
@Data
public class Date {
    private java.util.Date date;
    private List<Calendar> calendarItemList;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}