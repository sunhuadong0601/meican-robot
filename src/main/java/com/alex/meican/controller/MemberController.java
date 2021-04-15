package com.alex.meican.controller;


import com.alex.meican.dao.model.Member;
import com.alex.meican.exception.BusinessException;
import com.alex.meican.service.MemberService;
import com.alex.meican.vo.MemberVO;
import com.alex.meican.vo.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sunhuadong
 * @date 2020/5/12 9:24 下午
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class MemberController {

    @Resource
    private MemberService memberService;

    @PostMapping
    public ResponseEntity<?> create(MemberVO memberVO) {
        if (StringUtils.isBlank(memberVO.getPhone())) {
            return new ResponseEntity<>(ResponseData.error("手机号不能为空"), HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(memberVO.getCorpName())) {
            return new ResponseEntity<>(ResponseData.error("订餐地点不能为空"), HttpStatus.BAD_REQUEST);
        }
        // 用户名密码登录
        if (StringUtils.isBlank(memberVO.getCookies())) {
            if (StringUtils.isBlank(memberVO.getUsername()) || StringUtils.isBlank(memberVO.getPassword())) {
                return new ResponseEntity<>(ResponseData.error("用户名密码不能为空"), HttpStatus.BAD_REQUEST);
            }
            if (!memberVO.getUsername().contains(".com")) {
                return new ResponseEntity<>(ResponseData.error("用户名不正确"), HttpStatus.BAD_REQUEST);
            }
        }
        try {
            memberService.create(memberVO);
            return new ResponseEntity<>(ResponseData.success(null), HttpStatus.CREATED);
        } catch (BusinessException e) {
            log.error("UserController.add Exception:{}", e.getMessage());
            return new ResponseEntity<>(ResponseData.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> delete(Member member) {
        if (StringUtils.isBlank(member.getUsername())) {
            return new ResponseEntity<>(ResponseData.error("用户名不能为空"), HttpStatus.BAD_REQUEST);
        }
        memberService.delete(member);
        return new ResponseEntity<>(ResponseData.success(null), HttpStatus.OK);
    }
}
