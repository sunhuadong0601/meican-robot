package com.alex.meican.dto.request;

import com.alex.meican.dao.model.Dish;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/15 11:54 下午
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateParam {
    List<Dish> dishList;
    String corpName;
    String tabUUID;
    String tabName;
    String operativeDate;
    String closeTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
