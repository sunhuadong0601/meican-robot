package com.alex.meican.dao.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:20 上午
 */
@Data
@Entity
@Table
@DynamicInsert
@DynamicUpdate
public class Dish {
    @Id
    @Column
    private Long id;
    @Column
    private String name;
    @Column
    private int priceInCent;
    @Column
    private String priceString;
    @Column
    private int originalPriceInCent;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    private Restaurant restaurant;


    @Transient
    private boolean isSection;
    @Transient
    private String actionRequiredLevel;
    @Transient
    private String actionRequiredReason;
    @Transient
    private Long dishSectionId;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}