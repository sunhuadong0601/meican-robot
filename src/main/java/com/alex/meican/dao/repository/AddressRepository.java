package com.alex.meican.dao.repository;

import com.alex.meican.dao.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:35 下午
 */
public interface AddressRepository extends JpaRepository<Address,String> {
}
