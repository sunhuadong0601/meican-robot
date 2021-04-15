package com.alex.meican;

import com.alex.meican.service.MemberService;
import com.alex.meican.vo.MemberVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author sunhuadong
 * @date 2020/5/12 9:01 下午
 */
@SpringBootTest
public class MemberServiceTests {

    @Resource
    private MemberService memberService;

    @Test
    public void createTest(){
        MemberVO memberVO = MemberVO.builder()
                .username("")
                .password("")
                .autoLunch(true)
                .autoDinner(true)
                .phone("")
                .corpName("2009")
                .build();
        memberService.create(memberVO);
    }
}
