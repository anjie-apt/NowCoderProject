package com.company.toutiao.async;

import com.alibaba.fastjson.JSONObject;
import com.company.toutiao.utils.JedisAdapter;
import com.company.toutiao.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try {
            /**实现异步队列有两种方式
             * 1.BlockingQueue
             * 2.Redis，通过序列化反序列化进行消息传递
             */
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQUeueKey();
            jedisAdapter.lpush(key, json);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
