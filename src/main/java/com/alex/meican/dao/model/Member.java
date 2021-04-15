package com.alex.meican.dao.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author sunhuadong
 * @date 2020/5/12 11:52 下午
 */
@Data
@Accessors(chain = true)
@Entity
@Table
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(length = 32)
    private String username;

    @Column(length = 64)
    private String password;

    @Column(length = 1024)
    private String cookies;

    @Column(length = 1024)
    private String token;

    @Column(length = 11)
    private String phone;

    @Column(nullable = false)
    private Boolean checkLunch = false;

    @Column(nullable = false)
    private Boolean autoLunch = false;

    @Column(nullable = false)
    private Boolean checkDinner = false;

    @Column(nullable = false)
    private Boolean autoDinner = false;

    @Column(nullable = false)
    private String corpName = "";

    private Strategy strategy = Strategy.auto;

    @Column
    private Status status;

    @CreatedDate
    @Column
    private Date created;

    public enum Strategy {
        /**
         * 自动推荐
         */
        auto,
        /**
         * 订过次数最多
         */
        most;
    }

    public enum Status {
        /**
         * 正常
         */
        normal,
        /**
         * 登录过期
         */
        expire,
        /**
         * 暂停
         */
        pause
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
