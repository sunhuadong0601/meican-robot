package com.alex.meican.service;

import com.alex.meican.dao.model.*;
import com.alex.meican.dao.repository.*;
import com.alex.meican.dto.AccountsEntrance;
import com.alex.meican.dto.Order;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author sunhuadong
 * @date 2020/5/15 1:44 下午
 */
@Service
@Slf4j
public class LogService {

    @Resource
    private CorpRepository corpRepository;
    @Resource
    private RestaurantRepository restaurantRepository;
    @Resource
    private DishRepository dishRepository;
    @Resource
    private ClosetShowOrderRepository closetShowOrderRepository;
    @Resource
    private UserOrderRepository userOrderRepository;
    @Resource
    private AddressRepository addressRepository;

    /**
     * 保存AccountsEntrance中的Corp列表
     *
     * @param accountsEntrance AccountsEntrance
     */
    public void logAccountsEntrance(AccountsEntrance accountsEntrance) {
        List<Corp> corps = Lists.newArrayList();
        if (Objects.nonNull(accountsEntrance.getCorpGroupList())) {
            corps.addAll(accountsEntrance.getCorpGroupList());
        }
        if (Objects.nonNull(accountsEntrance.getOtherCorpList())) {
            corps.addAll(accountsEntrance.getOtherCorpList());
        }
        if (!CollectionUtils.isEmpty(corps)) {
            for (Corp corp : corps) {
                if (Objects.isNull(corp.getUniqueId())) {
                    corp.setUniqueId(corp.getNamespace());
                }
            }
            corpRepository.saveAll(corps);
        }
    }

    /**
     * 保存Dish
     *
     * @param dish dish
     */
    public void logDish(Dish dish) {
        if (Objects.nonNull(dish)) {
            dishRepository.saveAndFlush(dish);
        }
    }

    /**
     * 保存餐馆列表
     *
     * @param restaurantList restaurantList
     */
    public void logRestaurantList(List<Restaurant> restaurantList) {
        for (Restaurant restaurant : restaurantList) {
            logRestaurant(restaurant);
        }
    }

    /**
     * 保存餐馆
     *
     * @param restaurant restaurant
     */
    public void logRestaurant(Restaurant restaurant) {
        if (Objects.nonNull(restaurant)) {
            restaurantRepository.saveAndFlush(restaurant);
        }
    }

    /**
     * 保存Corp
     *
     * @param corp corp
     */
    public void logCorp(Corp corp) {
        if (Objects.nonNull(corp)) {
            corpRepository.saveAndFlush(corp);
        }
    }

    /**
     * 保存address
     *
     * @param address address
     */
    public void logAddress(Address address) {
        if (Objects.nonNull(address)) {
            addressRepository.saveAndFlush(address);
        }
    }

    /**
     * 保存下单后的信息
     *
     * @param member     member
     * @param closetShow closetShow
     */
    public void logClosetShow(Member member, String closetShow) {
        ClosetShowOrder closetShowOrder = new ClosetShowOrder();
        closetShowOrder.setMember(member);
        closetShowOrder.setClosetShow(closetShow);
        closetShowOrder.setCreated(new Date());
        closetShowOrderRepository.saveAndFlush(closetShowOrder);
    }

    /**
     * 保存用户订单列表
     *
     * @param member       member
     * @param orderList    orderList
     * @param isAutoPlaced isAutoPlaced
     */
    public void logUserOrder(Member member, List<Order> orderList, boolean isAutoPlaced) {
        for (Order order : orderList) {
            logUserOrder(member, order, isAutoPlaced);
        }
    }

    /**
     * 保存用户订单
     *
     * @param member       member
     * @param order        order
     * @param isAutoPlaced isAutoPlaced
     */
    public void logUserOrder(Member member, Order order, boolean isAutoPlaced) {
        Corp corp = order.getCorp();
        if (Objects.isNull(corp.getUniqueId())) {
            corp.setUniqueId(corp.getNamespace());
        }
        Restaurant restaurant = order.getRestaurantItemList().get(0);
        // 如果Restaurant表中已有则不更新
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(restaurant.getUniqueId());
        if (!optionalRestaurant.isPresent()) {
            restaurant = restaurantRepository.saveAndFlush(restaurant);
        }
        Dish dish = restaurant.getDishItemList().get(0).getDish();
        dish.setRestaurant(restaurant);
        Dish savedDish = dishRepository.saveAndFlush(dish);

        UserOrder userOrder = new UserOrder();
        userOrder.setUniqueId(order.getUniqueId());
        userOrder.setMember(member);
        userOrder.setCorp(corp);
        userOrder.setDish(savedDish);
        userOrder.setTimestamp(order.getTimestamp());
        userOrder.setAutoPlaced(isAutoPlaced);
        userOrder.setCreated(new Date());
        userOrderRepository.saveAndFlush(userOrder);
    }
}
