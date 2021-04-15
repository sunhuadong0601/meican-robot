package com.alex.meican.dto;

import com.alex.meican.dao.model.Dish;
import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:20 上午
 */
@Data
public class DishItem {
    private Dish dish;
    private int count;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}