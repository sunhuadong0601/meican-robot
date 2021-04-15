package com.alex.meican;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.dao.model.Corp;
import com.alex.meican.dao.model.Dish;
import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.model.Restaurant;
import com.alex.meican.dao.repository.MemberRepository;
import com.alex.meican.dto.AccountsEntrance;
import com.alex.meican.dto.Calendar;
import com.alex.meican.dto.ClosetOrder;
import com.alex.meican.dto.response.RestaurantResponse;
import com.alex.meican.exception.BusinessException;
import com.alex.meican.service.LogService;
import com.alex.meican.service.PreorderService;
import com.alex.meican.service.TaskService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:30 上午
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("prod")
public class PreorderServiceTests {
    @Resource
    private PreorderService preorderService;
    @Resource
    private MemberRepository memberRepository;
    @Resource
    private TaskService taskService;
    @Resource
    private LogService logService;

    private Member member;

    @BeforeEach
    void getUser() {
        member = memberRepository.findAll().get(0);
    }

    @Test
    void getCalendarTest() {
        List<Calendar> calendarList = preorderService.getCalendarList(member, DateUtils.addDays(new Date(), -3), DateUtils.addDays(new Date(), 1));
        String pretty = JSON.toJSONString(calendarList, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        log.info(pretty);
    }

    @Test
    void getAccountsEntranceTest() {
        AccountsEntrance accountsEntrance = preorderService.getAccountsEntrance(member);
        log.info("accountsEntrance:{}", accountsEntrance);
    }

    @Test
    void getRestaurantTest() {
        List<Calendar> calendarList = preorderService.getCalendarList(member, new Date());
        for (Calendar calendar : calendarList) {
            String uniqueId = calendar.getUserTab().getUniqueId();
            String closeTime = calendar.getOpeningTime().getCloseTime();
            String targetTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd") + "+" + closeTime;
            RestaurantResponse restaurantResponse = preorderService.getRestaurant(member, uniqueId, targetTime);
            log.info("restaurantResponse:{}", restaurantResponse);
        }
    }

    @Test
    void getCorpTest() {
        List<Calendar> calendarList = preorderService.getCalendarList(member, new Date());
        for (Calendar calendar : calendarList) {
            String namespace = calendar.getUserTab().getCorp().getNamespace();
            Corp corp = preorderService.getCorp(member, namespace);
            log.info("corp:{}", corp);
        }
    }

    @Test
    void getDishListTest() {
        List<Dish> dishList = taskService.dishResponse2List(preorderService.getDishResponse(member, "bbbcc5b3-3ab9-4abf-b9bb-49a507b749b7", OpenTime.dinner));
        if (CollectionUtils.isEmpty(dishList)) {
            throw new BusinessException(BusinessException.BusinessStatus.nonDish);
        }
        // 保存餐馆和菜品
        for (Dish dish : dishList) {
            Restaurant restaurant = dish.getRestaurant();
            logService.logRestaurant(restaurant);
            logService.logDish(dish);
        }
    }

    @Test
    void getOrderTest() {
//        Order order = preorderService.getOrder(member, "38d6d7e4ef1d", "CORP_ORDER", true);
//        log.info(order);
        ClosetOrder closetOrder = preorderService.getClosetOrder(member, "38d6d7e4ef1d");
        log.info("closetOrder:{}", closetOrder);
    }

}
