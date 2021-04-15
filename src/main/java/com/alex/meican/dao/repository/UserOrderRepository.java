package com.alex.meican.dao.repository;

import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.model.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:35 下午
 */
public interface UserOrderRepository extends JpaRepository<UserOrder,String> {
    List<UserOrder> findAllByMember(Member member);
}
