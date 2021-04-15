package com.alex.meican.task;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.model.WeekLunchCheck;
import com.alex.meican.dao.repository.MemberRepository;
import com.alex.meican.dao.repository.WeekMenuRepository;
import com.alex.meican.exception.BusinessException;
import com.alex.meican.dto.custom.CorpMenu;
import com.alex.meican.dto.custom.DayMenu;
import com.alex.meican.dto.custom.WeekMenu;
import com.alex.meican.service.RobotService;
import com.alex.meican.service.TaskService;
import com.alex.meican.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author sunhuadong
 * @date 2020/12/3 1:25 上午
 */
@Component
@Slf4j
public class WeekTask {
    @Resource
    private MemberRepository memberRepository;
    @Resource
    private TaskService taskService;
    @Resource
    private WeekMenuRepository weekMenuRepository;
    @Resource
    private RobotService robotService;


    /**
     * 检查是否存在下周一午餐
     * 如果存在则获取下周一到周五的午餐菜单
     * 每周六至周日每隔30秒执行一次
     */
    @Scheduled(cron = "0/30 * * ? * 6,7")
    public void lunchCheckTask() {
        WeekLunchCheck weekLunchCheck = weekMenuRepository.findByCheckDateGreaterThan(new Date());
        // 如果检查记录已存在 则不执行
        if (Objects.nonNull(weekLunchCheck)) {
            return;
        }
        List<Member> members = memberRepository.findAllByStatus(Member.Status.normal);
        for (Member member : members) {
            log.info("检查下周一午餐");
            try {
                Date nextMonday = DateUtil.getNextMonday();
                boolean hasNextMondayLunch = taskService.hasAvailableCalendars(member, OpenTime.lunch, nextMonday);
                // 如果周一存在午餐 则检查一周的午餐 并发送通知
                if (hasNextMondayLunch) {
                    log.info("找到可以订的午餐");
                    WeekMenu nextWeekMenu = taskService.getNextWeekMenu(member, OpenTime.lunch);
                    sendRobotMessage(nextWeekMenu);
                    weekLunchCheck = new WeekLunchCheck().setCheckDate(DateUtil.getNextMonday()).setCreated(new Date());
                    weekMenuRepository.saveAndFlush(weekLunchCheck);
                } else {
                    log.info("无可订午餐");
                }
                // 成功一次之后不再执行
                return;
            } catch (BusinessException e) {
                log.error("checkNextMondayLunch exception", e);
            }
            log.info("检查，尝试下一个用户");
        }
    }

    private void sendRobotMessage(WeekMenu weekMenu) {
        boolean hasDish = checkHasDish(weekMenu);
        if (hasDish) {
            log.info("发送机器人提醒");
            robotService.sendRobotMessageAsync("可以订下周的午餐了～");
        }
    }

    private boolean checkHasDish(WeekMenu weekMenu) {
        boolean hasDish = false;
        for (DayMenu dayMenu : weekMenu.getDayMenus()) {
            for (CorpMenu corpMenu : dayMenu.getCorpMenus()) {
                for (String dishName : corpMenu.getDishNames()) {
                    if (StringUtils.isNotBlank(dishName)) {
                        hasDish = true;
                    }
                }
            }
        }
        return hasDish;
    }


}
