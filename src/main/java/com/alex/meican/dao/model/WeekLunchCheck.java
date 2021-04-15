package com.alex.meican.dao.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author sunhuadong
 * @date 2020/12/5 11:49 上午
 */
@Data
@Accessors(chain = true)
@Entity
@Table
public class WeekLunchCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Date checkDate;
    @Column
    private Date created;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
