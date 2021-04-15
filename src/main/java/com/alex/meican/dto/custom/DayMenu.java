package com.alex.meican.dto.custom;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/12/5 11:14 上午
 */
@Data
public class DayMenu {

    private Integer dayOfWeek;
    private List<CorpMenu> corpMenus = Lists.newArrayList();

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
