package com.alex.meican.dto;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/15 12:51 上午
 */
@Data
public class ClosetOrder {
    private String title;
    private long corpOrderId;
    private String orderUniqueId;
    private String uniqueId;
    private boolean showPrice;
    private String closetOpenTime;
    private String closetAvailableStartTime;
    private String closetAvailableEndTime;
    private long targetTime;
    private int totalPriceInCent;
    private String statusInfo;
    private Warning warning;
    private String pickUpLocation;
    private String pickUpLocationCode;
    private List<OrderDishInBox> orderDishInBoxList;
    private List<String> orderDishWithClosetInfoList;
    private List<String> orderDishOverflow;
    private String closetCode;
    private boolean needShowClosetCode;
    private List<Progress> progressList;
    private Rating rating;
    private boolean isClosetCleared;
    private String closetVersion;
    private String realName;
    private String pickUpMessage;
    private boolean firstTimeUseCloset;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
