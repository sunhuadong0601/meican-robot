package com.alex.meican;

import com.alex.meican.constant.OpenTime;
import com.alex.meican.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author sunhuadong
 * @date 2020/6/3 5:44 下午
 */
@Slf4j
@SpringBootTest
public class UtilTests {
    @Test
    void testGetTimestamp() {
        log.info(DateUtil.getOpenTimeString(OpenTime.dinner));
    }
}
