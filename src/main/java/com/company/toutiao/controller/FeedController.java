package com.company.toutiao.controller;

import com.company.toutiao.model.EntityType;
import com.company.toutiao.model.Feed;
import com.company.toutiao.model.HostHolder;
import com.company.toutiao.service.FeedService;
import com.company.toutiao.service.FollowService;
import com.company.toutiao.utils.JedisAdapter;
import com.company.toutiao.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
/**
 * TimeLine:
 * 1.事件触发产生新鲜事
 * 2.粉丝新鲜事列表获取
 * 3.各新闻事自定义渲染
 * 4.新鲜事排序显示
 * 5.广告推荐整合
 */
public class FeedController {
    @Autowired
    FeedService feedService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = {"/pullfeeds"}, method = RequestMethod.GET)
    public String getPullFeeds(Model model) {
        //先判断是不是登录用户
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        //如果已登录，先获取我关注了多少人
        if (localUserId != 0) {
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        //再把我关注的人对应的feed流都拉出来，添加到model中返回，属于拉取模式实现feed流
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/pushfeeds"}, method = RequestMethod.GET)
    public String getPushFeeds(Model model) {
        //先判断是不是登录用户
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();

        //如果已登录，
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getFeedById(Integer.parseInt(feedId));
            if (feed == null) continue;
            feeds.add(feed);
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }
}
