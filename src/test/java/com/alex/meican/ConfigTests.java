package com.alex.meican;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author sunhuadong
 * @date 2020/5/14 1:30 上午
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("prod")
public class ConfigTests {

    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessSecret}")
    private String accessSecret;
    @Value("${meican.oauth2.clientId}")
    private String clientId;
    @Value("${meican.oauth2.clientSecret}")
    private String clientSecret;
    @Value("${wechat.robot.key}")
    private String robotKey;

    @Test
    void testConfigValue() {
        log.info("accessKeyId:{}", accessKeyId);
        log.info("accessSecret:{}", accessSecret);
        log.info("clientId:{}", clientId);
        log.info("clientSecret:{}", clientSecret);
        log.info("robotKey:{}", robotKey);
    }

}
