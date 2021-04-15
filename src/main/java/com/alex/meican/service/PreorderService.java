package com.alex.meican.service;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.constant.UrlConstant;
import com.alex.meican.dao.model.Corp;
import com.alex.meican.dao.model.Dish;
import com.alex.meican.dao.model.Member;
import com.alex.meican.exception.BusinessException;
import com.alex.meican.dto.*;
import com.alex.meican.dto.request.AddOrderParam;
import com.alex.meican.dto.request.CartUpdateParam;
import com.alex.meican.dto.response.*;
import com.alex.meican.util.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:13 上午
 */
@Service
@Slf4j
public class PreorderService {

    @Resource
    private OkHttpClient okHttpClient;

    public AccountsEntrance getAccountsEntrance(Member member) {
        Request request = new Request.Builder().url(UrlConstant.ACCOUNTS_ENTRANCE_URL).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    BaseResponse<AccountsEntrance> accountsEntranceBaseResponse = JSON.parseObject(body, new TypeReference<BaseResponse<AccountsEntrance>>() {
                    });
                    return accountsEntranceBaseResponse.getData();
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.getAccountsEntrance Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    public List<Calendar> getCalendarList(Member member) {
        return getCalendarList(member, new Date());
    }

    public List<Calendar> getCalendarList(Member member, Date date) {
        return getCalendarList(member, date, date);
    }

    public List<Calendar> getCalendarList(Member member, Date beginDate, Date endDate) {
        HttpUrl httpUrl = new Request.Builder().url(UrlConstant.CALENDAR_ITEMS_LIST_URL).build().url().newBuilder()
                .addEncodedQueryParameter("beginDate", DateFormatUtils.format(beginDate, "yyyy-MM-dd"))
                .addEncodedQueryParameter("endDate", DateFormatUtils.format(endDate, "yyyy-MM-dd"))
                .addEncodedQueryParameter("withOrderDetail", "false")
                .build();
        Request request = new Request.Builder().url(httpUrl).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (Objects.equals(responseBody.contentType(), MediaType.parse("application/json"))) {
                    String body = responseBody.string();
                    CalendarResponse calendarResponse = JSON.parseObject(body, CalendarResponse.class);
                    return getCalendarList(calendarResponse);
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.getCalendar Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    private List<Calendar> getCalendarList(CalendarResponse calendarResponse) {
        List<Calendar> calendars = Lists.newArrayList();
        calendarResponse.getDateList().forEach(date -> calendars.addAll(date.getCalendarItemList()));
        return calendars;
    }

    public RestaurantResponse getRestaurant(Member member, String tabUniqueId, OpenTime openTime) {
        return getRestaurant(member, tabUniqueId, DateUtil.getOpenTimeString(openTime));
    }

    public RestaurantResponse getRestaurant(Member member, String tabUniqueId, String targetTime) {
        HttpUrl httpUrl = new Request.Builder().url(UrlConstant.RESTAURANTS_LIST_URL).build().url().newBuilder()
                .addEncodedQueryParameter("tabUniqueId", tabUniqueId)
                .addEncodedQueryParameter("targetTime", targetTime)
                .build();
        Request request = new Request.Builder().url(httpUrl).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    return JSON.parseObject(body, RestaurantResponse.class);
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.getRestaurant Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    public Corp getCorp(Member member, String namespace) {
        HttpUrl httpUrl = new Request.Builder().url(UrlConstant.CORPS_SHOW_URL).build().url().newBuilder()
                .addQueryParameter("namespace", namespace)
                .build();
        Request request = new Request.Builder().url(httpUrl).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    return JSON.parseObject(body, Corp.class);
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.getCorp Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }


    /**
     * 获取当天17：00的菜单
     *
     * @param member      member
     * @param tabUniqueId tabUniqueId
     * @return DishResponse
     */
    public DishResponse getDishResponse(Member member, String tabUniqueId, OpenTime openTime) {
        return getDishResponse(member, tabUniqueId, DateUtil.getOpenTimeString(openTime));
    }

    /**
     * 获取指定时间的菜单
     *
     * @param member      member
     * @param tabUniqueId tabUniqueId
     * @param targetTime  targetTime
     * @return return
     */
    public DishResponse getDishResponse(Member member, String tabUniqueId, String targetTime) {
        HttpUrl httpUrl = new Request.Builder().url(UrlConstant.RECOMMENDATIONS_DISHES_URL).build().url().newBuilder()
                .addEncodedQueryParameter("tabUniqueId", tabUniqueId)
                .addEncodedQueryParameter("targetTime", targetTime)
                .build();
        Request request = new Request.Builder().url(httpUrl).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    return JSON.parseObject(body, DishResponse.class);
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.getDishResponse Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    public CartQueryResponse cartQuery(Member member, String closeTime, String tabUUID) {
        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("closeTime", closeTime)
                .addEncoded("tabUUID", tabUUID)
                .build();
        Request request = new Request.Builder().url(UrlConstant.CART_QUERY_URL).header("Cookie", member.getCookies()).post(requestBody).build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    return JSON.parseObject(body, CartQueryResponse.class);
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.cartQuery Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    public String cartUpdate(Member member, CartUpdateParam param) {
        JSONArray dishJsonArray = new JSONArray();
        for (Dish dish : param.getDishList()) {
            JSONObject dishJsonObject = new JSONObject();
            dishJsonObject.put("corpRestaurantId", dish.getRestaurant().getUniqueId());
            dishJsonObject.put("count", 1);
            dishJsonObject.put("name", dish.getName());
            dishJsonObject.put("priceInCent", dish.getPriceInCent());
            dishJsonObject.put("revisionId", dish.getId());
            dishJsonArray.add(dishJsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dishes", dishJsonArray);
        jsonObject.put("corpName", param.getCorpName());
        jsonObject.put("tabUUID", param.getTabUUID());
        jsonObject.put("tabName", param.getTabName());
        jsonObject.put("operativeDate", param.getOperativeDate());
        JSONObject json = new JSONObject();
        json.put(param.getTabUUID() + "/" + param.getCloseTime(), jsonObject);
        String data = json.toJSONString();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);
        Request request = new Request.Builder().url(UrlConstant.CART_UPDATE_URL).header("Cookie", member.getCookies()).post(requestBody).build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    return responseBody.string();
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.cartUpdate Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    @Async
    public void forwardLog(Member member) {
        HttpUrl httpUrl = new Request.Builder().url(UrlConstant.FORWARD_LOG_URL).build().url().newBuilder()
                .addEncodedQueryParameter("msg", "start+adding+order")
                .addEncodedQueryParameter("level", "log")
                .addEncodedQueryParameter("time", String.valueOf(System.currentTimeMillis()))
                .build();
        Request request = new Request.Builder().url(httpUrl).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody body = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(body)) {
                String result = body.string();
                log.info("forwardLog:" + result);
            }
        } catch (IOException e) {
            log.error("forwardLog Exception:" + e.getMessage());
        }
    }

    public AddOrderResponse addOrder(Member member, AddOrderParam param) {
        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("corpAddressRemark", param.getCorpAddressRemark())
                .addEncoded("corpAddressUniqueId", param.getCorpAddressRemark())
                .addEncoded("order", param.getOrder())
                .addEncoded("remarks", param.getRemarks())
                .addEncoded("tabUniqueId", param.getTabUniqueId())
                .addEncoded("targetTime", param.getTargetTime())
                .addEncoded("userAddressUniqueId", param.getUserAddressUniqueId())
                .build();
        Request request = new Request.Builder().url(UrlConstant.ORDERS_ADD_URL).header("Cookie", member.getCookies()).post(requestBody).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() == 400) {
                throw new BusinessException(BusinessException.BusinessStatus.networkException);
            }
            ResponseBody responseBody = response.body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    return JSON.parseObject(body, AddOrderResponse.class);
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.addOrder Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    public MultiCorpAddress getMultiCorpAddress(Member member, String namespace) {
        HttpUrl httpUrl = new Request.Builder().url(UrlConstant.GET_MULTI_CORP_ADDRESS_URL).build().url().newBuilder()
                .addEncodedQueryParameter("namespace", namespace)
                .build();
        Request request = new Request.Builder().url(httpUrl).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    BaseResponse<MultiCorpAddress> multiCorpAddressBaseResponse = JSON.parseObject(body, new TypeReference<BaseResponse<MultiCorpAddress>>() {
                    });
                    return multiCorpAddressBaseResponse.getData();
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.getMultiCorpAddress Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    public Order getOrder(Member member, String uniqueId, String type, boolean progressMarkdownSupport) {
        HttpUrl httpUrl = new Request.Builder().url(UrlConstant.ORDERS_SHOW_URL).build().url().newBuilder()
                .addEncodedQueryParameter("uniqueId", uniqueId)
                .addEncodedQueryParameter("type", type)
                .addEncodedQueryParameter("progressMarkdownSupport", String.valueOf(progressMarkdownSupport))
                .addEncodedQueryParameter("x", String.valueOf(System.currentTimeMillis()))
                .build();
        Request request = new Request.Builder().url(httpUrl).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    return JSON.parseObject(body, Order.class);
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.getOrder Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }

    public ClosetOrder getClosetOrder(Member member, String uniqueId) {
        HttpUrl httpUrl = new Request.Builder().url(UrlConstant.ORDERS_CLOSET_SHOW_URL).build().url().newBuilder()
                .addEncodedQueryParameter("uniqueId", uniqueId)
                .build();
        Request request = new Request.Builder().url(httpUrl).header("Cookie", member.getCookies()).get().build();
        try {
            ResponseBody responseBody = okHttpClient.newCall(request).execute().body();
            if (Objects.nonNull(responseBody)) {
                if (!Objects.equals(responseBody.contentType(), MediaType.parse("text/html; charset=utf-8"))) {
                    String body = responseBody.string();
                    BaseResponse<ClosetOrder> closetOrderBaseResponse = JSON.parseObject(body, new TypeReference<BaseResponse<ClosetOrder>>() {
                    });
                    return closetOrderBaseResponse.getData();
                }
                throw new BusinessException(BusinessException.BusinessStatus.cookieExpire);
            }
        } catch (IOException e) {
            log.error("PreorderService.getClosetOrder Exception:{}", e.getMessage());
        }
        throw new BusinessException(BusinessException.BusinessStatus.networkException);
    }
}
