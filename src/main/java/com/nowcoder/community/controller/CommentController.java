package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add/{discussPostId}")
    public ModelAndView addComment(@PathVariable int discussPostId, Comment comment, ModelAndView mv) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        commentService.addComment(comment);
        // redirect回帖子详情页面
        mv.setViewName("redirect:/discussPost/detail/" + discussPostId);
        return mv;
    }
}
