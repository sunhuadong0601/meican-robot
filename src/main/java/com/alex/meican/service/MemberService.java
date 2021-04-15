package com.alex.meican.service;

import com.alex.meican.dao.model.Member;
import com.alex.meican.dao.repository.MemberRepository;
import com.alex.meican.vo.MemberVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * @author sunhuadong
 * @date 2020/5/12 10:08 下午
 */
@Service
public class MemberService {

    @Resource
    private MemberRepository memberRepository;

    @Resource
    private OauthService oauthService;

    public void create(MemberVO memberVO) {
        Member member;
        if (StringUtils.isNotBlank(memberVO.getCookies())) {
            member = oauthService.loginByCookies(memberVO.getCookies());
        } else {
            member = oauthService.loginByUsernameAndPassword(memberVO.getUsername(), memberVO.getPassword());
        }
        Member targetMember = memberRepository.findByUsername(member.getUsername());
        if (Objects.nonNull(targetMember)) {
            member.setId(targetMember.getId());
        }
        member.setPhone(memberVO.getPhone());
        if (Objects.nonNull(memberVO.getCheckLunch())) {
            member.setCheckLunch(memberVO.getCheckLunch());
        }
        if (Objects.nonNull(memberVO.getAutoLunch())) {
            member.setAutoLunch(memberVO.getAutoLunch());
        }
        if (Objects.nonNull(memberVO.getCheckDinner())) {
            member.setCheckDinner(memberVO.getCheckDinner());
        }
        if (Objects.nonNull(memberVO.getAutoDinner())) {
            member.setAutoDinner(memberVO.getAutoDinner());
        }
        member.setCorpName(memberVO.getCorpName());
        String strategy = memberVO.getStrategy();
        if ("auto".equals(strategy)) {
            member.setStrategy(Member.Strategy.auto);
        } else if ("most".equals(strategy)) {
            member.setStrategy(Member.Strategy.most);
        }
        member.setStatus(Member.Status.normal);
        memberRepository.save(member);
    }

    public void delete(Member member) {
        Optional<Member> result = memberRepository.findById(member.getId());
        if (result.isPresent()) {
            Member currentMember = result.get();
            currentMember.setStatus(Member.Status.pause);
            memberRepository.saveAndFlush(currentMember);
        }
    }
}
