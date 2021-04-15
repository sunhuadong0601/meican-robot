package com.alex.meican.dao.repository;

import com.alex.meican.dao.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sunhuadong
 * @date 2020/5/12 11:00 下午
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUsername(String username);
    List<Member> findAllByStatus(Member.Status status);
}
