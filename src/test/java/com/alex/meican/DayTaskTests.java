package com.alex.meican;

import com.alex.meican.task.DayTask;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author sunhuadong
 * @date 2020/5/18 1:13 下午
 */
@SpringBootTest
public class DayTaskTests {

    @Resource
    private DayTask dayTask;


    @Test
    void preloadTaskTest() {
        dayTask.lunchPreloadTask();
        dayTask.dinnerPreloadTask();
    }

    @Test
    void checkTaskTest() {
        dayTask.lunchCheckTask();
        dayTask.dinnerCheckTask();
    }

    @Test
    void placeTaskTest() {
        dayTask.lunchPlaceTask();
        dayTask.dinnerPlaceTask();
    }

    @Test
    void allTaskTest() {
        preloadTaskTest();
        checkTaskTest();
        placeTaskTest();
    }
}
