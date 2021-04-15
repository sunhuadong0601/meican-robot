package com.alex.meican.service;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.dao.model.*;
import com.alex.meican.dao.repository.UserOrderRepository;
import com.alex.meican.exception.BusinessException;
import com.alex.meican.dto.Calendar;
import com.alex.meican.dto.MultiCorpAddress;
import com.alex.meican.dto.Order;
import com.alex.meican.dto.custom.CorpMenu;
import com.alex.meican.dto.custom.DayMenu;
import com.alex.meican.dto.custom.WeekMenu;
import com.alex.meican.dto.request.AddOrderParam;
import com.alex.meican.dto.request.CartUpdateParam;
import com.alex.meican.dto.response.AddOrderResponse;
import com.alex.meican.dto.response.CartQueryResponse;
import com.alex.meican.dto.response.DishResponse;
import com.alex.meican.dto.response.RestaurantResponse;
import com.alex.meican.util.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author sunhuadong
 * @date 2020/5/15 1:05 下午
 */
@Service
@Slf4j
public class TaskService {

    @Resource
    private SmsService smsService;
    @Resource
    private LogService logService;
    @Resource
    private PreorderService preorderService;
    @Resource
    private AsyncService asyncService;
    @Resource
    private UserOrderRepository userOrderRepository;

    /**
     * 提前获取今日的菜单
     *
     * @param member member
     * @return 是否成功 如果cookie过期则为false 其他为true
     */
    public boolean preLoadData(Member member, OpenTime openTime) {
        try {
            List<Calendar> calendarList = preorderService.getCalendarList(member);
            for (Calendar calendar : calendarList) {
                Corp corp = calendar.getUserTab().getCorp();
                // 保存corp
                logService.logCorp(corp);
                List<Address> addressList = corp.getAddressList();
                for (Address address : addressList) {
                    // 保存address
                    logService.logAddress(address);
                }
                String uniqueId = calendar.getUserTab().getUniqueId();
                List<Dish> dishList = dishResponse2List(preorderService.getDishResponse(member, uniqueId, openTime));
                if (!CollectionUtils.isEmpty(dishList)) {
                    // 保存餐馆和菜品
                    for (Dish dish : dishList) {
                        Restaurant restaurant = dish.getRestaurant();
                        logService.logRestaurant(restaurant);
                        logService.logDish(dish);
                    }
                }
                // 保存详细餐馆信息
                RestaurantResponse restaurant = preorderService.getRestaurant(member, uniqueId, openTime);
                List<Restaurant> restaurantList = restaurant.getRestaurantList();
                logService.logRestaurantList(restaurantList);
            }
        } catch (BusinessException e) {
            if (BusinessException.BusinessStatus.cookieExpire.equals(e.getStatus())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当天下单状态
     *
     * @param member member
     * @return boolean
     */
    public boolean checkIsPlacedOrder(Member member, OpenTime openTime) {
        return checkIsPlacedOrder(member, new Date(), openTime);
    }

    /**
     * 检查指定日期下单状态
     *
     * @param member   member
     * @param date     date
     * @param openTime openTime
     * @return boolean
     */
    public boolean checkIsPlacedOrder(Member member, Date date, OpenTime openTime) {
        // 1.请求accountsEntrance(这一步可省)
//        AccountsEntrance accountsEntrance = preorderService.getAccountsEntrance(member);
//        logService.logAccountsEntrance(accountsEntrance);
        // 2.请求calendarList
        List<Calendar> calendarList = preorderService.getCalendarList(member, date);
        return checkIsPlacedOrder(member, calendarList, openTime);
    }

    /**
     * 检查指定calendarList下单状态
     *
     * @param member       member
     * @param calendarList calendarList
     * @param openTime     openTime
     * @return boolean
     */
    public boolean checkIsPlacedOrder(Member member, List<Calendar> calendarList, OpenTime openTime) {
        // 如果为空则直接返回
        if (CollectionUtils.isEmpty(calendarList)) {
            return false;
        }
        // 获取今天已下订单列表 为空说明今天没有下单
        List<Order> orderList = getUserOrderList(calendarList);
        if (!CollectionUtils.isEmpty(orderList)) {
            logService.logUserOrder(member, orderList, false);
            for (Order order : orderList) {
                String openingTimeName = order.getCorp().getOpeningTimeList().get(0).getName();
                if (openTime.getName().equals(openingTimeName)) {
                    Corp corp = order.getCorp();
                    Dish dish = order.getRestaurantItemList().get(0).getDishItemList().get(0).getDish();
                    log.info("用户:{}, 今日已订{}:{}, 取餐地点:{}", member.getUsername(), openTime.getName(), dish.getName(), corp.getName());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取订单列表
     *
     * @param calendarList calendarList
     * @return List<Order>
     */
    private List<Order> getUserOrderList(List<Calendar> calendarList) {
        List<Order> orderList = Lists.newArrayList();
        for (Calendar calendar : calendarList) {
            Order corpOrderUser = calendar.getCorpOrderUser();
            if (Objects.nonNull(corpOrderUser)) {
                orderList.add(corpOrderUser);
            }
        }
        return orderList;
    }

    public void sendMessage(Member member, String message) {
        log.info("发送消息给{}, {}", member.getUsername(), message);
    }

    /**
     * 下单方法
     *
     * @param member member
     * @return boolean
     */
    public boolean placeOrder(Member member, OpenTime openTime) {
        // 1.检查用户是否已经订餐
        List<Calendar> calendarList = preorderService.getCalendarList(member, new Date());
        boolean isPlacedOrder = checkIsPlacedOrder(member, calendarList, openTime);
        if (isPlacedOrder) {
            return true;
        }
        log.debug("用户:{}, calendarList:{}", member.getUsername(), calendarList);
        // 选择对应时间和公司地点的calendar
        Calendar calendar = chooseCalendar(member, calendarList, openTime);
        // 生成需要的参数
        Map<String, String> paramMap = createParam(calendar);
        // 2.执行cartQuery
        CartQueryResponse cartQueryResponse = preorderService.cartQuery(member, paramMap.get("closeTime"), paramMap.get("tabUniqueId"));
        log.debug("用户:{}, cartQueryResponse:{}", member.getUsername(), cartQueryResponse);
        // 3.获取菜品列表
        List<Dish> dishList = dishResponse2List(preorderService.getDishResponse(member, paramMap.get("tabUniqueId"), openTime));
        if (CollectionUtils.isEmpty(dishList)) {
            throw new BusinessException(BusinessException.BusinessStatus.nonDish);
        }
        // 保存餐馆和菜品
        for (Dish dish : dishList) {
            Restaurant restaurant = dish.getRestaurant();
            logService.logRestaurant(restaurant);
            logService.logDish(dish);
        }
        // 选择菜品
        Dish dish = chooseUserDish(member, dishList);
        // 4.执行cartUpdate
        CartUpdateParam cartUpdateParam = CartUpdateParam.builder()
                .dishList(Lists.newArrayList(dish))
                .corpName(paramMap.get("corpName"))
                .tabUUID(paramMap.get("tabUniqueId"))
                .tabName(paramMap.get("calendarTitle"))
                .operativeDate(paramMap.get("operativeDate"))
                .closeTime(paramMap.get("closeTime"))
                .build();
        String cartUpdateResponse = preorderService.cartUpdate(member, cartUpdateParam);
        log.debug("用户:{}, cartUpdate:{}", member.getUsername(), cartUpdateResponse);
        // 5.执行forwardLog(可省)
        preorderService.forwardLog(member);
        // 6.执行getMultiCorpAddress
        MultiCorpAddress multiCorpAddress = preorderService.getMultiCorpAddress(member, paramMap.get("corpNamespace"));
        // 获取地址的UniqueId
        String addressUniqueId = multiCorpAddress.getAddressList().get(0).getFinalValue().getUniqueId();
        // 生成下单所需参数
        AddOrderParam addOrderParam = createAddOrderParam(addressUniqueId, dish, paramMap);
        // 7.下单
        AddOrderResponse addOrderResponse = preorderService.addOrder(member, addOrderParam);
        log.debug("用户:{}, addOrderResponse:{}", member.getUsername(), addOrderResponse);
        if (!"SUCCESSFUL".equals(addOrderResponse.getStatus())) {
            log.error("下单失败，用户:{}, {}", member, addOrderResponse.getMessage());
            return false;
        }
        // 发送订餐成功短信
        smsService.sendPlaceSms(member, paramMap.get("corpName"), dish.getName(), openTime);
        // 8.查询订单
        Order order = preorderService.getOrder(member, addOrderResponse.getOrder().getUniqueId(), "CORP_ORDER", true);
        logService.logUserOrder(member, order, true);
        // 异步任务
        asyncService.getAndLogClosetShowOrder(member, addOrderResponse.getOrder().getUniqueId());
        return true;
    }

    /**
     * 根据用户保存的信息选择公司地点，返回Calendar
     *
     * @param member       member
     * @param calendarList calendarList
     * @return Calendar
     */
    public Calendar chooseCalendar(Member member, List<Calendar> calendarList, OpenTime openTime) {
        String autoPlaceCorpName = member.getCorpName();
        if (CollectionUtils.isEmpty(calendarList)) {
            throw new BusinessException(BusinessException.BusinessStatus.nonDish);
        }
        // 去除不可订餐状态
        calendarList.removeIf(calendar -> !"AVAILABLE".equals(calendar.getStatus()));
        // 去除非当前时段
        calendarList.removeIf(calendar -> !openTime.getName().equals(calendar.getOpeningTime().getName()));
        if (CollectionUtils.isEmpty(calendarList)) {
            throw new BusinessException(BusinessException.BusinessStatus.nonDish);
        }
        log.info("用户:{}, 根据用户设定选择地点", member.getUsername());
        Calendar calendar = getCalendarsCorpNameContains(calendarList, autoPlaceCorpName);
        if (Objects.isNull(calendar)) {
            calendar = calendarList.get(0);
            log.info("用户:{}, 未找到匹配的公司地点,默认选择第一个地点:{}", member.getUsername(), calendar.getTitle());
        } else {
            log.info("用户:{}, 找到匹配的公司地点:{}", member.getUsername(), calendar.getTitle());
        }
        return calendar;
    }

    /**
     * 通过用户设置的corp名称匹配Calendar
     *
     * @param calendarList calendarList
     * @param corpName     corpName
     * @return Calendar
     */
    private Calendar getCalendarsCorpNameContains(List<Calendar> calendarList, String corpName) {
        for (Calendar calendar : calendarList) {
            String title = calendar.getTitle();
            if (title.contains(corpName)) {
                return calendar;
            }
        }
        return null;
    }

    /**
     * 生成所需要的参数
     *
     * @param calendar calendar
     * @return Map<String, String>
     */
    private Map<String, String> createParam(Calendar calendar) {
        Map<String, String> result = Maps.newHashMap();
        result.put("calendarTitle", calendar.getTitle());
        result.put("corpName", calendar.getUserTab().getCorp().getName());
        result.put("corpNamespace", calendar.getUserTab().getCorp().getNamespace());
        result.put("corpAddressUniqueId", calendar.getUserTab().getCorp().getAddressList().get(0).getUniqueId());
        result.put("tabUniqueId", calendar.getUserTab().getUniqueId());
        result.put("operativeDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
        result.put("closeTime", result.get("operativeDate") + " " + calendar.getOpeningTime().getCloseTime());
        return result;
    }

    /**
     * DishResponse转Dish列表
     *
     * @param dishResponse dishResponse
     * @return List<Dish>
     */
    public List<Dish> dishResponse2List(DishResponse dishResponse) {
        List<Dish> dishList = Lists.newArrayList();
        List<Dish> myRegularDishList = dishResponse.getMyRegularDishList();
        if (Objects.nonNull(myRegularDishList)) {
            dishList.addAll(myRegularDishList);
        }
        List<Dish> othersRegularDishList = dishResponse.getOthersRegularDishList();
        if (Objects.nonNull(othersRegularDishList)) {
            dishList.addAll(othersRegularDishList);
        }
        return dishList;
    }

    /**
     * 根据用户策略 选择用户想要的餐品
     *
     * @param dishList dishList
     * @return Dish
     */
    public Dish chooseUserDish(Member member, List<Dish> dishList) {
        Dish chosenDish = null;
        Member.Strategy strategy = member.getStrategy();
        log.info("用户:{}, 根据用户策略选择餐品,策略为:{}", member.getUsername(), strategy.name());
        if (Member.Strategy.most.equals(strategy)) {
            // 查找用户点餐记录
            List<UserOrder> userOrders = userOrderRepository.findAllByMember(member);
            if (!CollectionUtils.isEmpty(userOrders)) {
                // 按名称统计出每个菜名的下单次数
                Map<Object, Long> dishCountMap = userOrders.stream().
                        collect(Collectors.groupingBy(userOrder -> {
                            String dishName = userOrder.getDish().getName();
                            String shortDishName = dishName.substring(0, dishName.indexOf("("));
                            return StringUtils.isNotBlank(shortDishName) ? shortDishName : dishName;
                        }, Collectors.counting()));
                int maxCount = 0;
                // 遍历菜单中的每个菜，找到下单次数最多的菜
                for (Dish dish : dishList) {
                    String dishName = dish.getName();
                    String shortDishName = dishName.substring(0, dishName.indexOf("("));
                    dishName = StringUtils.isNotBlank(shortDishName) ? shortDishName : dishName;
                    Long count = dishCountMap.get(dishName);
                    if (Objects.nonNull(count)) {
                        if (count.intValue() >= maxCount) {
                            // 当前最多的菜
                            chosenDish = dish;
                            // 最多的次数
                            maxCount = count.intValue();
                        }
                    }
                }
            }
        } else if (Member.Strategy.auto.equals(strategy)) {
            chosenDish = dishList.get(0);
        } else {
            log.error("策略不存在, 自动选择模式");
            chosenDish = dishList.get(0);
        }
        if (Objects.isNull(chosenDish)) {
            log.error("根据用户策略没有选择到合适的菜品, 自动选择模式");
            chosenDish = dishList.get(0);
        }
        log.info("用户:{}, 选择了:{}", member.getUsername(), chosenDish.getName());
        return chosenDish;
    }

    /**
     * 生成下单所需的参数
     *
     * @param addressUniqueId addressUniqueId
     * @param dish            dish
     * @param paramMap        paramMap
     * @return AddOrderParam
     */
    public AddOrderParam createAddOrderParam(String addressUniqueId, Dish dish, Map<String, String> paramMap) {
        AddOrderParam addOrderParam = new AddOrderParam();
        addOrderParam.setCorpAddressRemark("");
        addOrderParam.setCorpAddressUniqueId(addressUniqueId);

        JSONObject orderJsonObject = new JSONObject();
        orderJsonObject.put("count", 1);
        orderJsonObject.put("dishId", dish.getId());
        JSONArray orderJsonArray = new JSONArray();
        orderJsonArray.add(orderJsonObject);
        addOrderParam.setOrder(orderJsonArray.toJSONString());

        JSONObject remarkJsonObject = new JSONObject();
        remarkJsonObject.put("dishId", dish.getId());
        remarkJsonObject.put("remark", "");
        JSONArray remarkJsonArray = new JSONArray();
        remarkJsonArray.add(remarkJsonObject);
        addOrderParam.setRemarks(remarkJsonArray.toJSONString());

        addOrderParam.setTabUniqueId(paramMap.get("tabUniqueId"));
        addOrderParam.setTargetTime(paramMap.get("closeTime"));
        addOrderParam.setUserAddressUniqueId(paramMap.get("corpAddressUniqueId"));
        return addOrderParam;
    }

    /**
     * 检查指定日期是否有可以订的餐
     *
     * @param member   member
     * @param openTime 午餐或晚餐
     * @param date     日期
     * @return 返回true或false
     */
    public boolean hasAvailableCalendars(Member member, OpenTime openTime, Date date) {
        List<Calendar> calendarList = preorderService.getCalendarList(member, date, date);
        // 过滤
        List<Calendar> calendars = filterOutAvailableCalendars(calendarList, openTime);
        return CollectionUtils.isNotEmpty(calendars);
    }

    /**
     * 从calendars中过滤出可以点的餐
     *
     * @param calendars calendar列表
     * @param openTime  午餐或晚餐
     * @return 可以点的餐
     */
    private List<Calendar> filterOutAvailableCalendars(List<Calendar> calendars, OpenTime openTime) {
        if (CollectionUtils.isNotEmpty(calendars)) {
            return calendars.stream()
                    .filter(calendar -> openTime.getName().equals(calendar.getOpeningTime().getName()))
                    .filter(calendar -> "AVAILABLE".equals(calendar.getStatus()))
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    /**
     * 获取下周一周的菜单
     *
     * @param member   member
     * @param openTime 午餐或晚餐
     * @return 返回WeekMenu
     */
    public WeekMenu getNextWeekMenu(Member member, OpenTime openTime) {
        WeekMenu weekMenu = new WeekMenu();
        Date nextMondayDate = DateUtil.getNextMonday();
        // 周一到周五
        for (int dayOfWeek = 1; dayOfWeek <= 5; dayOfWeek++) {
            Date date = DateUtils.addDays(nextMondayDate, dayOfWeek - 1);
            List<Calendar> availableLunch = filterOutAvailableCalendars(preorderService.getCalendarList(member, date), openTime);
            DayMenu dayMenu = new DayMenu();
            dayMenu.setDayOfWeek(dayOfWeek);
            for (Calendar calendar : availableLunch) {
                CorpMenu corpMenu = new CorpMenu();
                corpMenu.setCorpName(calendar.getUserTab().getName());
                String openTimeString = DateUtil.getOpenTimeString(OpenTime.lunch, date);
                List<Dish> dishes = dishResponse2List(preorderService.getDishResponse(member, calendar.getUserTab().getUniqueId(), openTimeString));
                for (Dish dish : dishes) {
                    corpMenu.getDishNames().add(dish.getName());
                }
                dayMenu.getCorpMenus().add(corpMenu);
            }
            weekMenu.getDayMenus().add(dayMenu);
        }
        return weekMenu;
    }

}