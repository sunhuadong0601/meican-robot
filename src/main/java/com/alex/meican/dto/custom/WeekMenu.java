package com.alex.meican.dto.custom;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/12/5 11:27 上午
 */
@Data
public class WeekMenu {

    private List<DayMenu> dayMenus = Lists.newArrayList();

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
