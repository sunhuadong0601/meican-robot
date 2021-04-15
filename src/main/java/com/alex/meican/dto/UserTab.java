package com.alex.meican.dto;

import com.alex.meican.dao.model.Corp;
import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:20 上午
 */
@Data
//@Table
//@Entity
//@DynamicInsert
//@DynamicUpdate
public class UserTab {
//    @Id
//    @Column
    private String uniqueId;
//    @Column
    private String name;
//    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
//            fetch = FetchType.LAZY)
//    @JoinColumn(foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    private Corp corp;
//    @Column
    private Long lastUsedTime;

//    @Transient
    private String latitude;
//    @Transient
    private String longitude;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}