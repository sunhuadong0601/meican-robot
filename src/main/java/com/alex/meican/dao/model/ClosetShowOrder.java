package com.alex.meican.dao.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author sunhuadong
 * @date 2020/5/19 2:33 上午
 */
@Data
@Table
@Entity
public class ClosetShowOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Column(columnDefinition = "text")
    private String closetShow;

    @Column
    private Date created;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
