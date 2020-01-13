package com.company.toutiao.service;

import com.company.toutiao.utils.JedisAdapter;
import com.company.toutiao.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import javax.print.attribute.IntegerSyntax;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean follow(int userId, int entityType, int entityId) {
        /**
         * 逻辑
         * 1.把我的关注对象放到关注列表里
         * 2.把自己放到实体的粉丝列表
         */
        //生成Redis的key，关注者的key由
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        Date date = new Date();
        //使用Redis事务将添加关注列表和粉丝列表一起执行
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //使用zset作为存储的数据结构，按照关注时间作为排序依据score
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));

        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long)ret.get(1) > 0;

    }

    public boolean unfollow(int userId, int entityType, int entityId) {
        /**
         * 逻辑
         * 1.从我的关注列表中移除关注对象
         * 2.从关注对象的粉丝列表中移除我
         */
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

        Date date = new Date();

        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);

        tx.zrem(followerKey, String.valueOf(userId));
        tx.zrem(followeeKey, String.valueOf(entityId));

        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (Long) ret.get(0) > 0 && (Long)ret.get(1) > 0;
    }

    private List<Integer> getIdsFromSet(Set<String> idset) {
        List<Integer> ids = new ArrayList<>();
        for (String str : idset) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    public List<Integer> getFollowers(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
    }

    public List<Integer> getFollowees(int entityType, int entityId, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));
    }

    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long getFolloweeCount(int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return jedisAdapter.zcard(followeeKey);
    }

    public boolean isFollwer(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }

    //TODO:实现共同关注
}
