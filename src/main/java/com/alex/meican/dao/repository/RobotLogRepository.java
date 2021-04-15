package com.alex.meican.dao.repository;

import com.alex.meican.dao.model.RobotLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author sunhuadong
 * @date 2020/12/5 11:49 上午
 */
public interface RobotLogRepository extends JpaRepository<RobotLog,Long> {
}
