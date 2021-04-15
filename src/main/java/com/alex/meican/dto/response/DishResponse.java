package com.alex.meican.dto.response;

import com.alex.meican.dao.model.Dish;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/15 1:20 下午
 */
@Data
public class DishResponse {
    private List<Dish> myRegularDishList;
    private List<Dish> othersRegularDishList;
    private String othersRegularDishListSource;
    private boolean showPrice;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
