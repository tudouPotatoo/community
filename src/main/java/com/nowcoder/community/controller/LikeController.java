package com.nowcoder.community.controller;

import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    /**
     * 点赞
     * @param entityType
     * @param entityId
     * @return 将最新的点赞状态和赞的数量封装成json返回
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId) {
        return CommunityUtil.getJsonString(SERVER_SUCCESS, null, likeService.like(entityType, entityId));
    }
}
