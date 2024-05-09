package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class HomeController {
    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 获取主页面数据
     * @param mv
     * @return
     */
    @GetMapping("/index")
    public ModelAndView getIndexPage(ModelAndView mv, Page page) {
        // 设置分页信息
        // 为什么Page对象不用注入ModelAndView对象呢，因为在方法调用前，SpringMVC会自动实例化ModelAndView和Page，并将page注入ModelAndView
        // 因此page已经在ModelAndView中了，不需要再次注入
        page.setRows(discussPostService.getDiscussPostCount(0));
        page.setPath("/index");

        List<Map<String, Object>> postAndUserInfo = new ArrayList<>();
        // 1. 获取所有帖子信息
        List<DiscussPost> discussPosts = discussPostService.getDiscussPosts(0, page.getOffset(), page.getPageSize());
        // 2. 根据帖子获取对应用户信息
        for (DiscussPost post : discussPosts) {
            // 获取用户信息
            User user = userService.getUserById(post.getUserId());
            // 将帖子及对应用户信息绑定存入列表
            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            map.put("post", post);
            postAndUserInfo.add(map);
        }
        // 4. 将数据信息添加到ModelAndView
        mv.addObject("postAndUserInfo", postAndUserInfo);

        // 5. 将页面信息添加到ModelAndView
        mv.setViewName("index");
        return mv;
    }

    /**
     * 报错时跳转至500页面
     * @return
     */
    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }
}
