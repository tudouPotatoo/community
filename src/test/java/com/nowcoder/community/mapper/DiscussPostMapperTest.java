package com.nowcoder.community.mapper;

import com.nowcoder.community.CommunityApplication;
import com.nowcoder.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {CommunityApplication.class})
public class DiscussPostMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

    }

    @Test public void testSelectDiscussPostRows() {
        int count = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(count);
    }

}
