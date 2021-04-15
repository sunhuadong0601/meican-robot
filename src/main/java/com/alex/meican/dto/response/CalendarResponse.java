package com.alex.meican.dto.response;

import com.alex.meican.dto.Date;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:20 上午
 */
@Data
public class CalendarResponse {
    private java.util.Date startDate;
    private java.util.Date endDate;
    private List<Date> dateList;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
