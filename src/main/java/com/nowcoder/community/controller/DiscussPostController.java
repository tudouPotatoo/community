package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discussPost")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

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

    /**
     * 根据id获取帖子详情信息
     * 1. 根据帖子id获取帖子信息
     * 2. 根据用户id获取用户信息
     * 3. 将帖子信息、用户信息都封装到Model中
     * 4. 获取帖子的评论信息，并封装到Model中
     * 5. 返回帖子详情页面
     *
     * @param id
     * @param mv
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public ModelAndView getDiscussPostDetail(@PathVariable("discussPostId") int id, ModelAndView mv, Page commentPage) {
        // 1. 根据帖子id获取帖子信息
        DiscussPost discussPost = discussPostService.getDiscussPostById(id);
        // 2. 根据用户id获取用户信息
        User user = userService.getUserById(discussPost.getUserId());
        // 3. 将帖子信息、用户信息都封装到Model中 返回帖子详情页面
        mv.addObject("post", discussPost);
        mv.addObject("user", user);

        // 评论分页信息
        commentPage.setRows(discussPost.getCommentCount());
        commentPage.setPageSize(5);
        commentPage.setPath("/discussPost/detail/" + id);

        // 4. 获取帖子的评论信息，并封装到Model中
        List<Comment> commentList = commentService.getCommentByEntityId(ENTITY_TYPE_POST, id, commentPage.getOffset(), commentPage.getPageSize());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        // 遍历每一条评论
        for (Comment comment : commentList) {
            Map<String, Object> commentVo = new HashMap<>();
            // 获取评论本身
            commentVo.put("comment", comment);
            // 获取评论用户
            User commentUser = userService.getUserById(comment.getUserId());
            commentVo.put("user", commentUser);
            // 获取评论回复总数
            int replyCount = commentService.getCountByEntityId(ENTITY_TYPE_COMMENT, comment.getId());
            commentVo.put("replyCount", replyCount);
            // 获取回复列表
            List<Comment> replyList = commentService.getCommentByEntityId(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
            List<Map<String, Object>> replyVoList = new ArrayList<>();
            // 遍历每一条回复
            for (Comment reply : replyList) {
                Map<String, Object> replyVo = new HashMap<>();
                // 获取回复本身
                replyVo.put("reply", reply);
                // 获取回复作者用户
                User replyUser = userService.getUserById(reply.getUserId());
                replyVo.put("user", replyUser);
                // 获取回复的目标用户
                User replyTargetUser = userService.getUserById(reply.getTargetId());
                replyVo.put("target", replyTargetUser);
                System.out.println("target: " + replyTargetUser);

                replyVoList.add(replyVo);
            }
            commentVo.put("replyVoList", replyVoList);
            commentVoList.add(commentVo);
        }

        mv.addObject("commentVoList", commentVoList);

        // 5. 返回帖子详情页面
        mv.setViewName("/site/discuss-detail");
        return mv;
    }
}
