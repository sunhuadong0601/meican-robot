package com.alex.meican.dao.repository;

import com.alex.meican.dao.model.WeekLunchCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author sunhuadong
 * @date 2020/12/5 11:49 上午
 */
public interface WeekMenuRepository extends JpaRepository<WeekLunchCheck, Long> {

    WeekLunchCheck findByCheckDateGreaterThan(Date today);

}
