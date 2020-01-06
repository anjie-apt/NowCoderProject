package com.company.toutiao.controller;

import com.company.toutiao.model.Comment;
import com.company.toutiao.model.EntityType;
import com.company.toutiao.model.HostHolder;
import com.company.toutiao.service.CommentService;
import com.company.toutiao.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;


/**
 * 通用的新模块开发流程
 * 1.数据库字段设计
 * 2.Model定义，基本与数据库相匹配
 * 3.DAO:数据读取
 * 4.Service:服务包装
 * 5.Controller:业务入口
 * 6.Test:功能测试
 */
@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = "/addComment", method = RequestMethod.POST )
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            if (hostHolder.getUser() != null) {
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            commentService.addComment(comment);
            logger.info("添加评论成功");
        } catch (Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
