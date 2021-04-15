package com.alex.meican.exception;

import lombok.Getter;

/**
 * @author sunhuadong
 * @date 2020/5/12 9:41 下午
 */
public class BusinessException extends RuntimeException {

    @Getter
    private final BusinessStatus status;

    @Getter
    public enum BusinessStatus {
        /**
         * cookie过期
         */
        cookieExpire("cookie过期"),
        /**
         * 网络异常
         */
        networkException("网络异常"),
        /**
         * 没有可以点的餐
         */
        nonDish("没有可以点的餐");

        private final String message;

        BusinessStatus(String message) {
            this.message = message;
        }
    }

    public BusinessException(BusinessStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
