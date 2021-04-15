package com.alex.meican.dto.request;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/15 11:53 下午
 */
@Data
public class AddOrderParam {
    private String corpAddressRemark;
    private String corpAddressUniqueId;
    private String order;
    private String remarks;
    private String tabUniqueId;
    private String targetTime;
    private String userAddressUniqueId;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
