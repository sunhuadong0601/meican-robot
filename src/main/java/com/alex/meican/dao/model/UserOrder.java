package com.alex.meican.dao.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author sunhuadong
 * @date 2020/5/21 11:14 下午
 */
@Data
@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
public class UserOrder {
    @Id
    @Column
    private String uniqueId;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "corp_unique_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Corp corp;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinColumn(name = "dish_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Dish dish;
    @Column
    private Long timestamp;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private Member member;
    @Column
    private boolean autoPlaced = false;
    @CreatedDate
    @Column
    private Date created;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
