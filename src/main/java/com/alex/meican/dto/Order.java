package com.alex.meican.dto;

import com.alex.meican.dao.model.Address;
import com.alex.meican.dao.model.Corp;
import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.model.Restaurant;
import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/*** @author sunhuadong* @date 2020/5/15 12:34 上午*/
@Data
public class Order {
    private String uniqueId;
    private Member member;
    private Warning warning;
    private Rating rating;
    private String title;
    private Long corpOrderId;
    private List<Restaurant> restaurantItemList;
    private Corp corp;
    private boolean readyToDelete;
    private Address corpAddress;
    private String closetPickupLocationCode;
    private String actionRequiredLevel;
    private Postbox postbox;
    private String cashPaid;
    private String cashPaidInCent;
    private String balancePaid;
    private String balancePaidInCent;
    private String alipayPaid;
    private String alipayPaidInCent;
    private String paidAlipayTradeNumber;
    private List<Progress> progressList;
    private String corpOrderStatus;
    private boolean acceptCashPaymentToMeican;
    private String totalPrice;
    private String totalPriceInCent;
    private String corpToMeicanPrice;
    private String corpToMeicanPriceInCent;
    private boolean isSalaryUserToMeicanPricePayment;
    private String userToCorpPrice;
    private String userToCorpPriceInCent;
    private String netCorpToMeicanSpending;
    private String netCorpToMeicanSpendingInCent;
    private String userToMeicanPrice;
    private int userToMeicanPriceInCent;
    private int corpToRestaurantPrice;
    private int corpToRestaurantPriceInCent;
    private int userToRestaurantPrice;
    private int userToRestaurantPriceInCent;
    private String user;
    private boolean showPrice;
    private String unpaidUserToMeicanPrice;
    private int unpaidUserToMeicanPriceInCent;
    private String cashPayShutdownNotice;
    private String paidUserToMeicanPrice;
    private int paidUserToMeicanPriceInCent;
    private String specialAccountPaidList;
    private String specialAccountUserList;
    private Long timestamp;
    private Long targetTime;
    private String tabUniqueId;
    private Long orderTime;
    private String corpAddressRemark;
    private String pickupMessage;
    private int payTimeoutInSeconds;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}