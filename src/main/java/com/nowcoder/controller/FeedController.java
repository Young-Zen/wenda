package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    private static final Logger logger=LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = {"/pullfeeds"},method = RequestMethod.GET)
    public String getPullFeeds(Model model){
        int localUserId=hostHolder.getUser()==null?0:hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<Integer>();
        if (localUserId != 0) {
            // 关注的人
            followees=followService.getFollowees(EntityType.ENTITY_USER,localUserId,Integer.MAX_VALUE);
        }
        List<Feed> feeds=feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/pushfeeds"},method = RequestMethod.GET)
    public String getPushFeeds(Model model){
        int localUserId=hostHolder.getUser()==null?0:hostHolder.getUser().getId();
        List<String> feedIds=jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds=new ArrayList<Feed>();
        for(String feedId:feedIds){
            Feed feed=feedService.getById(Integer.parseInt(feedId));
            if (feed == null) {
                continue;
            }
            feeds.add(feed);
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
}
