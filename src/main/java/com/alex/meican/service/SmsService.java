package com.alex.meican.service;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.model.SmsLog;
import com.alex.meican.dao.repository.SmsLogRepository;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author sunhuadong
 * @date 2020/5/19 1:39 下午
 */
@Service
@Slf4j
public class SmsService {

    @Resource
    private IAcsClient iAcsClient;
    @Resource
    private SmsLogRepository smsLogRepository;

    /**
     * 发送订餐提醒短信
     *
     * @param member member
     */
    public void sendRemindSms(Member member, OpenTime openTime) {
        String phone = member.getPhone();
        if (Objects.isNull(phone)) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", openTime.getName());
        try {
            CommonResponse response = sendSms(phone, "亚美文化", "SMS_192300338", jsonObject.toJSONString());
            log.info("用户:{}, 发送订餐提醒短信成功:{}", member.getUsername(), response.getData());
            SmsLog smsLog = JSONObject.parseObject(response.getData(), SmsLog.class).setMember(member).setContent("订餐提醒").setCreated(new Date());
            smsLogRepository.save(smsLog);
        } catch (ClientException e) {
            log.error("发送订餐提醒短信异常:{}", e.getMessage());
        }
    }

    /**
     * 发送订餐完成短信
     *
     * @param member   member
     * @param corpName corpName
     * @param dishName dishName
     */
    public void sendPlaceSms(Member member, String corpName, String dishName, OpenTime openTime) {
        String phone = member.getPhone();
        if (Objects.isNull(phone)) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        // 餐名不能超过20个字符
        if (dishName.length() > 20) {
            dishName = dishName.substring(0, dishName.indexOf("("));
            if (dishName.length() > 20) {
                dishName = dishName.substring(0, 20);
            }
        }
        jsonObject.put("dishName", dishName);
        jsonObject.put("corpName", corpName);
        String templateCode = null;
        try {
            if (OpenTime.lunch.equals(openTime)) {
                templateCode = "SMS_192300341";
            } else if (OpenTime.dinner.equals(openTime)) {
                templateCode = "SMS_192230778";
            }
            CommonResponse response = sendSms(phone, "亚美文化", templateCode, jsonObject.toJSONString());
            log.info("用户:{}, 发送订餐成功短信成功:{}", member.getUsername(), response.getData());
            SmsLog smsLog = JSONObject.parseObject(response.getData(), SmsLog.class).setMember(member).setContent("订餐提醒").setCreated(new Date());
            ;
            smsLogRepository.save(smsLog);
        } catch (ClientException e) {
            log.error("发送订餐成功短信异常:{}", e.getMessage());
        }
    }

    /**
     * 发送cookie过期提醒短信
     *
     * @param member member
     */
    public void sendCookieExpireSms(Member member) {
        String phone = member.getPhone();
        if (Objects.isNull(phone)) {
            return;
        }
        try {
            CommonResponse response = sendSms(phone, "亚美文化", "SMS_190725553", null);
            log.info("用户:{}, 发送cookie过期提醒短信成功:{}", member.getUsername(), response.getData());
            SmsLog smsLog = JSONObject.parseObject(response.getData(), SmsLog.class).setMember(member).setContent("cookie过期").setCreated(new Date());
            smsLogRepository.save(smsLog);
        } catch (ClientException e) {
            log.error("发送cookie过期提醒短信异常:{}", e.getMessage());
        }
    }

    /**
     * 发送短信方法
     *
     * @param phoneNumbers  手机号 逗号隔开
     * @param signName      签名
     * @param templateCode  模板
     * @param templateParam 模板参数
     * @return CommonResponse
     * @throws ClientException ClientException
     */
    private CommonResponse sendSms(String phoneNumbers, String signName, String templateCode, String templateParam) throws ClientException {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        if (Objects.nonNull(templateCode)) {
            request.putQueryParameter("TemplateParam", templateParam);
        }
        return iAcsClient.getCommonResponse(request);
    }


}
