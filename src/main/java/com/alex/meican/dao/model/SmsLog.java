package com.alex.meican.dao.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author sunhuadong
 * @date 2020/5/19 9:28 下午
 */
@Data
@Accessors(chain = true)
@Entity
@Table
public class SmsLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String message;
    @Column
    private String RequestId;
    @Column
    private String BizId;
    @Column
    private String Code;
    @ManyToOne
    @JoinColumn(name = "member_id",foreignKey = @ForeignKey(name = "none",value = ConstraintMode.NO_CONSTRAINT))
    private Member member;
    @Column
    private String content;
    @Column
    private Date created;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
