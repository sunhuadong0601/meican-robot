package com.alex.meican;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.repository.MemberRepository;
import com.alex.meican.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author sunhuadong
 * @date 2020/5/15 1:09 下午
 */
@Slf4j
@SpringBootTest
public class DayTaskServiceTests {
    @Resource
    private TaskService taskService;

    private Member member;

    @Resource
    private MemberRepository memberRepository;

    @BeforeEach
    void getUser() {
        member = memberRepository.findAllByStatus(Member.Status.normal).get(0);
    }

    @Test
    void checkIsPlacedOrderTest() {
//        boolean isOrderYesterday5 = taskService.checkIsPlacedOrder(member, DateUtils.addDays(new Date(), -5), OpenTime.dinner);
//        log.info(isOrderYesterday5);
//        boolean isOrderYesterday4 = taskService.checkIsPlacedOrder(member, DateUtils.addDays(new Date(), -4),OpenTime.dinner);
//        log.info(isOrderYesterday4);
//        boolean isOrderYesterday3 = taskService.checkIsPlacedOrder(member, DateUtils.addDays(new Date(), -3),OpenTime.dinner);
//        log.info(isOrderYesterday3);
//        boolean isOrderYesterday2 = taskService.checkIsPlacedOrder(member, DateUtils.addDays(new Date(), -2),OpenTime.dinner);
//        log.info(isOrderYesterday2);
//        boolean isOrderYesterday = taskService.checkIsPlacedOrder(member, DateUtils.addDays(new Date(), -1),OpenTime.dinner);
//        log.info(isOrderYesterday);
        boolean isOrderToday = taskService.checkIsPlacedOrder(member, new Date(), OpenTime.lunch);
        log.info("isOrderToday:{}", isOrderToday);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void placeOrderTest() {
        taskService.placeOrder(member, OpenTime.dinner);
    }
}
