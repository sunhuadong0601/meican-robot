package com.alex.meican.util;

import com.alex.meican.constant.OpenTime;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

/**
 * @author sunhuadong
 * @date 2020/5/15 10:31 下午
 */
public class DateUtil {

    public static String getOpenTimeString(OpenTime openTime) {
        return getOpenTimeString(openTime, new Date());
    }

    public static String getOpenTimeString(OpenTime openTime, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date) + "+" + openTime.getTime();
    }

    /**
     * 获取下周一的时间
     *
     * @return Date
     */
    public static Date getNextMonday() {
        return Date.from(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                .plusWeeks(1)
                .with(DayOfWeek.MONDAY)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    /**
     * 获取下周五的时间
     *
     * @return Date
     */
    public static Date getNextFriday() {
        return Date.from(LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                .plusWeeks(1)
                .with(DayOfWeek.FRIDAY)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
