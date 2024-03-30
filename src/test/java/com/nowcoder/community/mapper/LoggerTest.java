package com.nowcoder.community.mapper;

import com.nowcoder.community.CommunityApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {CommunityApplication.class})
@Slf4j
public class LoggerTest {
//    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger() {
//        System.out.println(logger.getName());
//        logger.debug("debug debug...");
//        logger.info("debug info...");
//        logger.warn("debug warn...");
//        logger.error("debug error...");

        System.out.println(log.getName());
        log.debug("debug debug...");
        log.info("debug info...");
        log.warn("debug warn...");
        log.error("debug error...");
    }
}
