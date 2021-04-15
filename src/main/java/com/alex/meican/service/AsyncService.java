package com.alex.meican.service;

import com.alex.meican.dao.model.Member;
import com.alex.meican.dto.ClosetOrder;
import com.alibaba.fastjson.JSON;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author sunhuadong
 * @date 2020/5/19 2:05 上午
 */
@Service
public class AsyncService {
    @Resource
    private PreorderService preorderService;
    @Resource
    private LogService logService;

    @Async
    public void getAndLogClosetShowOrder(Member member, String uniqueId) {
        ClosetOrder closetOrder = preorderService.getClosetOrder(member, uniqueId);
        logService.logClosetShow(member, JSON.toJSONString(closetOrder));
    }
}
