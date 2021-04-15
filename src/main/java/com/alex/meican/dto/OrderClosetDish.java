package com.alex.meican.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/15 12:52 上午
 */
@Data
public class OrderClosetDish {
    private long id;
    private String name;
    private String restaurantUniqueId;
    private String restaurantName;
    private int priceInCent;
    private int boxNumber;
    private String skuType;
    private String seq;
    private String labelStyle;
    private String labelUrl;
    private boolean userReceived;
    private String closetDishSkuDeployStatus;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
