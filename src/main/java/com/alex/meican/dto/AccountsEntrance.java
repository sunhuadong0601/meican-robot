package com.alex.meican.dto;

import com.alex.meican.dao.model.Corp;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/14 2:57 上午
 */
@Data
public class AccountsEntrance {
    private List<Corp> corpGroupList;
    private List<Corp> otherCorpList;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
