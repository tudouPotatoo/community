package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 增加一条帖子
     * 0. 校验用户是否登陆
     *      是 --> 继续往下
     *      否 --> 提示用户必须登陆才能够访问
     * 1. 校验title是否为空
     *      是 --> 提示title不能为空
     *      否 --> 继续往下
     * 2. 校验content是否为空
     *      是 --> 提示content不能为空
     *      否 --> 继续往下
     * 3. 调用DiscussPostService，将贴子存入数据库
     *
     * 返回状态：
     *          0-发布成功
     *         -1-存在异常
     * @param title 帖子的标题
     * @param content 帖子的内容
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        // 0. 校验用户是否登陆
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJsonString(403, "请先登陆！");
        }
        // 1. 校验title是否为空
        if (StringUtils.isBlank(title)) {
            return CommunityUtil.getJsonString(-1, "标题不能为空！");
        }
        // 2. 校验content是否为空
        if (StringUtils.isBlank(content)) {
            return CommunityUtil.getJsonString(-1, "正文不能为空！");
        }
        // 3. 调用DiscussPostService，将贴子存入数据库
        discussPostService.addDiscussPost(title, content);

        // TODO 异常后续统一处理
        return CommunityUtil.getJsonString(0, "发布成功！");
    }
}
