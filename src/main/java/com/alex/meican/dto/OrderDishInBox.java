package com.alex.meican.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/15 12:52 上午
 */
@Data
public class OrderDishInBox {
    private Closet closet;
    private List<OrderClosetDish> orderClosetDishList;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
