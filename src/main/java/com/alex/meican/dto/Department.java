package com.alex.meican.dto;

import com.alex.meican.dao.model.Corp;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/14 2:49 上午
 */
@Data
public class Department {

    private String uniqueId;
    private String name;
    private List<Corp> corp;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}