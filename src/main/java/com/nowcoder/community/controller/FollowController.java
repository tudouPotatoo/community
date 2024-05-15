package com.nowcoder.community.controller;

import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        followService.follow(entityType, entityId);
        return CommunityUtil.getJsonString(SERVER_SUCCESS, "关注成功！");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        followService.unfollow(entityType, entityId);
        return CommunityUtil.getJsonString(SERVER_SUCCESS, "已取消关注！");
    }

}
