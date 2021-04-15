package com.alex.meican.dto.response;

import com.alex.meican.dao.model.Dish;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/15 10:54 下午
 */
@Data
public class CartQueryResponse {
    private Long corpId;
    private String corpName;
    private String tabUUID;
    private String tabName;
    private String tabOpen;
    private String tabClose;
    private List<Dish> dishes;
    private String operativeDate;
    private Long generateTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
