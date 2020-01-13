package com.company.toutiao.controller;

import com.company.toutiao.async.EventModel;
import com.company.toutiao.async.EventProducer;
import com.company.toutiao.async.EventType;
import com.company.toutiao.model.*;
import com.company.toutiao.service.CommentService;
import com.company.toutiao.service.FollowService;
import com.company.toutiao.service.QuestionService;
import com.company.toutiao.service.UserService;
import com.company.toutiao.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class FollowController {

    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;

    /**
     * 根据传入的用户ID，调用关注服务完成关注操作
     * 再发送一个事件，最后把关注的结果返回
     * @param userId
     * @return
     */
    @RequestMapping(path = "/followUser", method = RequestMethod.POST )
    @ResponseBody
    public String follow(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).
                setActorId(hostHolder.getUser().getId()).
                setEntityType(EntityType.ENTITY_USER).
                setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));

    }

    @RequestMapping(path = "/unfollowUser", method = RequestMethod.POST )
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).
                setActorId(hostHolder.getUser().getId()).
                setEntityType(EntityType.ENTITY_USER).
                setEntityOwnerId(userId));
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));

    }

    @RequestMapping(path = "/followQuestion", method = RequestMethod.POST )
    @ResponseBody
    public String followQuestion(@RequestParam("QuestionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        Question question = questionService.selectById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).
                setActorId(hostHolder.getUser().getId()).
                setEntityType(EntityType.ENTITY_QUESTION).
                setEntityOwnerId(question.getId()));
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = "/unfollowQuestion", method = RequestMethod.POST )
    @ResponseBody
    public String unfollowQuestion(@RequestParam("QuestionId") int questionId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Question question = questionService.selectById(questionId);
        if (question == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).
                setActorId(hostHolder.getUser().getId()).
                setEntityType(EntityType.ENTITY_QUESTION).
                setEntityOwnerId(question.getId()));

        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    private List<ViewObject> getUsersInfo (int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, uid));
            //如果已登录，判断已登录用户是不是指定UID的粉丝，未登录就直接判断为否
            if (localUserId != 0) {
                vo.set("followed", followService.isFollwer(localUserId, EntityType.ENTITY_USER, uid));
            }
            else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }

    @RequestMapping(path = "/user/{uid}/followers", method = RequestMethod.GET)
    public String followers (Model model, @PathVariable("uid") int userId) {
        //获取关注者ID列表
        List<Integer> followerIds = followService.getFollowers(userId, EntityType.ENTITY_USER, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(userId, followerIds));
        }
        else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        return "followers";
    }


    @RequestMapping(path = "/user/{uid}/followees", method = RequestMethod.GET)
    public String followees (Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(userId, followeeIds));
        }
        else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        return "followees";
    }
}
