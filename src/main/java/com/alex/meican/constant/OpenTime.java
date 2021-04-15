package com.alex.meican.constant;

import lombok.Getter;

/**
 * @author sunhuadong
 * @date 2020/6/3 3:06 下午
 */
@Getter
public enum OpenTime {
    /**
     * 午餐
     */
    lunch("午餐", "10:00"),
    /**
     * 晚餐
     */
    dinner("晚餐", "17:00");

    private final String name;
    private final String time;

    OpenTime(String name, String time) {
        this.name = name;
        this.time = time;
    }

}
