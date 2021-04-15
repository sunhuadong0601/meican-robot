package com.alex.meican.dao.model;

import com.alex.meican.dto.DishItem;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/15 12:49 上午
 */
@Data
@Entity
@Table
@DynamicInsert
@DynamicUpdate
public class Restaurant {

    @Id
    @Column
    private String uniqueId;
    @Column
    private String name;
    @Column
    private String tel;
    @Column
    private double latitude;
    @Column
    private double longitude;

    @Transient
    private int rating;
    @Transient
    private String deliveryRangeMeter;
    @Transient
    private int minimumOrder;
    @Transient
    private String warning;
    @Transient
    private String openingTime;
    @Transient
    private boolean onlinePayment;
    @Transient
    private boolean open;
    @Transient
    private int availableDishCount;
    @Transient
    private int dishLimit;
    @Transient
    private int restaurantStatus;
    @Transient
    private boolean remarkEnabled;
    @Transient
    private List<DishItem> dishItemList;
    @Transient
    private boolean available;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
