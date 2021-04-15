package com.alex.meican;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.repository.MemberRepository;
import com.alex.meican.dto.custom.WeekMenu;
import com.alex.meican.service.TaskService;
import com.alex.meican.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author sunhuadong
 * @date 2020/12/3 1:46 上午
 */
@Slf4j
@SpringBootTest
public class WeekTaskTests {
    @Resource
    private TaskService taskService;
    @Resource
    private MemberRepository memberRepository;

    @Test
    public void testGetNextWeekDishInfo() {
        Member member = memberRepository.findAllByStatus(Member.Status.normal).get(0);
        WeekMenu weekMenu = taskService.getNextWeekMenu(member, OpenTime.lunch);
        log.info("weekMenu:{}", weekMenu);
    }

    @Test
    public void testCheckNextMondayLunch() {
        Member member = memberRepository.findAllByStatus(Member.Status.normal).get(0);
        boolean b = taskService.hasAvailableCalendars(member, OpenTime.lunch, DateUtil.getNextMonday());
        log.info("b:{}", b);
    }
}
