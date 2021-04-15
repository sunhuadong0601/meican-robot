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
public class Address {
    @Id
    @Column
    private String uniqueId;
    @Column
    private String address;
    @Column
    private String corpAddressCode;
    @Column
    private String pickUpLocation;
    @Transient
    private Corp corp;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}