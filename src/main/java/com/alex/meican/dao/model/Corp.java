package com.alex.meican.dao.model;

import com.alex.meican.dto.Department;
import com.alex.meican.dto.OpeningTime;
import com.alex.meican.dto.Payment;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

//import javax.persistence.*;

/**
 * @author sunhuadong
 * @date 2020/5/14 11:09 下午
 */
@Data
@Entity
@Table
@DynamicInsert
@DynamicUpdate
public class Corp {

    @Id
    @Column(unique = true)
    private String uniqueId;
    @Column
    private String name;
    @Column(unique = true)
    private String namespace;

    @Transient
    private boolean hasPostbox;
    @Transient
    private Long corpId;
    @Transient
    private boolean priceVisible;
    @Transient
    private boolean showPrice;
    @Transient
    private int priceLimitInCent;
    @Transient
    private long dishLimit;
    @Transient
    private boolean publicAccessible;
    @Transient
    private List<Department> departmentList;
    @Transient
    private List<Address> addressList;
    @Transient
    private String logoImageUrl;
    @Transient
    private String status;
    @Transient
    private boolean corpAdmin;
    @Transient
    private List<OpeningTime> openingTimeList;
    @Transient
    private boolean hasMealPointGroup;
    @Transient
    private List<String> mealPointList;
    @Transient
    private boolean alwaysOpen;
    @Transient
    private boolean useMultiCorpAddress;
    @Transient
    private boolean useCorpAddressRemark;
    @Transient
    private boolean useSpecialAccount;
    @Transient
    private boolean useCloset;
    @Transient
    private boolean remarkEnabled;
    @Transient
    private List<Payment> includedPayments;
    @Transient
    private List<Payment> excludedPayments;

    public static class paymentConvert implements AttributeConverter<List<Payment>, String> {
        @Override
        public String convertToDatabaseColumn(List<Payment> payments) {
            return JSON.toJSONString(payments);
        }

        @Override
        public List<Payment> convertToEntityAttribute(String s) {
            return JSON.parseArray(s, Payment.class);
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
