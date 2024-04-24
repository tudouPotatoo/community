package com.nowcoder.community.utils;

import com.nowcoder.community.CommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {CommunityApplication.class})
public class SensitiveWordsFilterTest {

    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

    @Test
    public void testFilter() {
        String str = "不可以赌博，不可以@嫖@娼@，可以吃饭，不可以开票！！！";
        String res = sensitiveWordsFilter.filter(str);
        System.out.println(res);
    }
}
