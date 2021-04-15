package com.alex.meican.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/6/3 6:03 下午
 */
@Data
@Builder
@AllArgsConstructor
public class MemberVO {
    private String username;
    private String password;
    private String cookies;
    private String phone;
    private String corpName;
    private String strategy;
    private Boolean checkLunch;
    private Boolean autoLunch;
    private Boolean checkDinner;
    private Boolean autoDinner;
}
