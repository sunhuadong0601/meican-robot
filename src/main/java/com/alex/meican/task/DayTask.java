package com.alex.meican.task;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.repository.MemberRepository;
import com.alex.meican.exception.BusinessException;
import com.alex.meican.service.SmsService;
import com.alex.meican.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sunhuadong
 * @date 2020/5/12 10:01 下午
 */
@Component
@Slf4j
public class DayTask {

    @Resource
    private MemberRepository memberRepository;
    @Resource
    private TaskService taskService;
    @Resource
    private SmsService smsService;

    /**
     * 午餐
     * 检查当天的菜品信息
     * 每周一至周五的9：00执行
     */
    @Scheduled(cron = "0 0 9 ? * 1,2,3,4,5")
    public void lunchPreloadTask() {
        List<Member> members = memberRepository.findAllByStatus(Member.Status.normal);
        for (Member member : members) {
            log.info("开始获取今天午餐的菜品信息");
            boolean isSuccess = taskService.preLoadData(member, OpenTime.lunch);
            // 成功一次之后不再执行
            if (isSuccess) {
                return;
            }
            log.info("获取失败，尝试下一个用户");
        }
    }

    /**
     * 午餐
     * 检查是否已订餐 如果没有订餐则发送提醒短信
     * 每周一至周五的9：30执行
     */
    @Scheduled(cron = "0 30 9 ? * 1,2,3,4,5")
    public void lunchCheckTask() {
        List<Member> members = memberRepository.findAllByStatus(Member.Status.normal);
        members = members.stream().filter(Member::getCheckLunch).collect(Collectors.toList());
        for (Member member : members) {
            try {
                // 检查有没有可以订的餐
                boolean hasAvailableCalendars = taskService.hasAvailableCalendars(member, OpenTime.lunch, new Date());
                if (hasAvailableCalendars) {
                    boolean isPlaced = checkOrder(member, OpenTime.lunch);
                    if (!isPlaced) {
                        taskService.sendMessage(member, "今天还没有午餐！");
                        smsService.sendRemindSms(member, OpenTime.lunch);
                    }
                }
            } catch (BusinessException e) {
                log.error("查询午餐订餐状态出现异常:{}", e.getMessage());
                if (BusinessException.BusinessStatus.cookieExpire.equals(e.getStatus())) {
                    member.setStatus(Member.Status.expire);
                    memberRepository.save(member);
                    taskService.sendMessage(member, "cookie已失效,请重新登录");
                    smsService.sendCookieExpireSms(member);
                }
            }
        }
    }

    /**
     * 午餐
     * 如果没有订餐则自动订餐并发送短信
     * 每周一至周五的9：50执行
     */
    @Scheduled(cron = "0 50 9 ? * 1,2,3,4,5")
    public void lunchPlaceTask() {
        List<Member> members = memberRepository.findAllByStatus(Member.Status.normal);
        members = members.stream().filter(Member::getAutoDinner).collect(Collectors.toList());
        for (Member member : members) {
            try {
                // 检查有没有可以订的餐
                boolean hasAvailableCalendars = taskService.hasAvailableCalendars(member, OpenTime.lunch, new Date());
                if (hasAvailableCalendars) {
                    boolean placedOrder = checkOrder(member, OpenTime.lunch);
                    if (!placedOrder) {
                        // 处理订餐
                        orderProcessor(member, OpenTime.lunch);
                    }
                }
            } catch (BusinessException e) {
                log.error("午餐自动订餐时发生异常:{}", e.getMessage());
                if (BusinessException.BusinessStatus.cookieExpire.equals(e.getStatus())) {
                    member.setStatus(Member.Status.expire);
                    memberRepository.save(member);
                    taskService.sendMessage(member, "cookie已失效,请重新登录");
                    smsService.sendCookieExpireSms(member);
                }
            }
        }
    }

    /**
     * 晚餐
     * 检查当天的菜品信息
     * 每周一至周五的16：00执行
     */
    @Scheduled(cron = "0 0 16 ? * 1,2,3,4,5")
    public void dinnerPreloadTask() {
        List<Member> members = memberRepository.findAllByStatus(Member.Status.normal);
        for (Member member : members) {
            log.info("开始获取今天晚餐的菜品信息");
            boolean isSuccess = taskService.preLoadData(member, OpenTime.dinner);
            // 成功一次之后不再执行
            if (isSuccess) {
                return;
            }
            log.info("获取失败，尝试下一个用户");
        }
    }

    /**
     * 晚餐
     * 检查是否已订餐 如果没有订餐则发送提醒短信
     * 每周一至周五的16：30执行
     */
    @Scheduled(cron = "0 30 16 ? * 1,2,3,4,5")
    public void dinnerCheckTask() {
        List<Member> members = memberRepository.findAllByStatus(Member.Status.normal);
        members = members.stream().filter(Member::getCheckDinner).collect(Collectors.toList());
        for (Member member : members) {
            try {
                // 检查有没有可以订的餐
                boolean hasAvailableCalendars = taskService.hasAvailableCalendars(member, OpenTime.dinner, new Date());
                if (hasAvailableCalendars) {
                    boolean isPlaced = checkOrder(member, OpenTime.dinner);
                    if (!isPlaced) {
                        taskService.sendMessage(member, "今天还没有订晚餐！");
                        smsService.sendRemindSms(member, OpenTime.dinner);
                    }
                }
            } catch (BusinessException e) {
                log.error("查询晚餐订餐状态出现异常:{}", e.getMessage());
                if (BusinessException.BusinessStatus.cookieExpire.equals(e.getStatus())) {
                    member.setStatus(Member.Status.expire);
                    memberRepository.save(member);
                    taskService.sendMessage(member, "cookie已失效,请重新登录");
                    smsService.sendCookieExpireSms(member);
                }
            }
        }
    }

    /**
     * 晚餐
     * 如果没有订餐则自动订餐并发送短信
     * 每周一至周五的16：50执行
     */
    @Scheduled(cron = "0 50 16 ? * 1,2,3,4,5")
    public void dinnerPlaceTask() {
        List<Member> members = memberRepository.findAllByStatus(Member.Status.normal);
        members = members.stream().filter(Member::getAutoDinner).collect(Collectors.toList());
        for (Member member : members) {
            try {
                // 检查有没有可以订的餐
                boolean hasAvailableCalendars = taskService.hasAvailableCalendars(member, OpenTime.dinner, new Date());
                if (hasAvailableCalendars) {
                    boolean placedOrder = checkOrder(member, OpenTime.dinner);
                    if (!placedOrder) {
                        // 处理订餐
                        orderProcessor(member, OpenTime.dinner);
                    }
                }
            } catch (BusinessException e) {
                log.error("晚餐自动订餐时发生异常:{}", e.getMessage());
                if (BusinessException.BusinessStatus.cookieExpire.equals(e.getStatus())) {
                    member.setStatus(Member.Status.expire);
                    memberRepository.save(member);
                    taskService.sendMessage(member, "cookie已失效,请重新登录");
                    smsService.sendCookieExpireSms(member);
                }
            }
        }
    }

    /**
     * 检查是否已经下单
     *
     * @param member member
     * @return boolean
     */
    public boolean checkOrder(Member member, OpenTime openTime) {
        log.info("用户:{}, 开始检查{}订餐状态", member.getUsername(), openTime.getName());
        boolean placedOrder = taskService.checkIsPlacedOrder(member, openTime);
        log.info("用户:{}, {}订餐状态:{}", member.getUsername(), openTime.getName(), placedOrder);
        return placedOrder;
    }

    /**
     * 下单处理
     *
     * @param member member
     */
    public void orderProcessor(Member member, OpenTime openTime) {
        if (member.getAutoLunch() && OpenTime.lunch.equals(openTime)
                || member.getAutoDinner() && OpenTime.dinner.equals(openTime)) {
            taskService.sendMessage(member, "开始自动下单!");
            boolean placed = taskService.placeOrder(member, openTime);
            if (placed) {
                taskService.sendMessage(member, "下单成功!");
            } else {
                taskService.sendMessage(member, "下单失败!");
            }
        }
    }
}
