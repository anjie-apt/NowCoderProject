package com.company.toutiao.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.company.toutiao.async.EventHandler;
import com.company.toutiao.async.EventModel;
import com.company.toutiao.async.EventType;
import com.company.toutiao.model.EntityType;
import com.company.toutiao.model.Feed;
import com.company.toutiao.model.Question;
import com.company.toutiao.model.User;
import com.company.toutiao.service.*;
import com.company.toutiao.utils.JedisAdapter;
import com.company.toutiao.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class FeedHandler implements EventHandler {
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel eventModel) {
        Map<String, String> map = new HashMap<>();
        User actor = userService.getUser(eventModel.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if (eventModel.getType() == EventType.COMMENT ||
                (eventModel.getType() == EventType.FOLLOW && eventModel.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.selectById(eventModel.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel eventModel) {

        //方便测试数据
        Random r = new Random();
        eventModel.setActorId(1 + r.nextInt(10));

        //根据传入的eventModel创建对应的feed,存储到数据库中，需要拉取的时候直接获取所有的关注人对应的feed
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(eventModel.getActorId());
        feed.setType(eventModel.getType().getValue());
        feed.setData(buildFeedData(eventModel));
        if (feed.getData() == null) {
            return;
        }

        feedService.addFeed(feed);

        //给事件的粉丝推送
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, eventModel.getActorId(), Integer.MAX_VALUE);
        //未登录的时候只能获取到系统账户相关内容
        followers.add(0);
        for (int follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT, EventType.FOLLOW);
    }
}
