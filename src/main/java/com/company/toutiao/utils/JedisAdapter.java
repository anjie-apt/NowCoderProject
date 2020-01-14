package com.company.toutiao.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.company.toutiao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@Component
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    public Jedis getJedis() {
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    logger.error("发生异常" + ioe.getMessage());
                }
            }
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zadd(String key, double score, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public void lpush (String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public List<String> brpop (int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.brpop(timeout, key);
        }
        catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public static void print(int index, Object object) {
        System.out.println(String.format("%d, %s", index, object.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();

        //K-V:单一数值，可以用于验证码、PV、缓存
        jedis.set("hello", "loneranger");
        print(1, jedis.get("hello"));
        //设置过期时间，常用于验证码（图形、短信）或者缓存
        jedis.setex("hello2", 10, "world");
        //用于简单的缓存数值计算
        jedis.set("pv", "100");
        jedis.incr("pv");
        jedis.incrBy("pv", 5);
        jedis.decrBy("pv", 2);
        print(2, jedis.keys("*"));

        //list:双向列表，适用于最新列表，关注列表
        String listName = "list";
        jedis.del(listName);
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(4, jedis.lrange(listName, 0, 12));
        print(4, jedis.lrange(listName, 0, 4));
        print(5, jedis.llen(listName));
        print(6, jedis.lpop(listName));
        print(7, jedis.llen(listName));
        print(8, jedis.lrange(listName, 2, 6));
        print(9, jedis.lindex(listName, 3));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xxx"));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "xxx"));
        print(11, jedis.lrange(listName, 0, 12));

        //hash:对象属性，不定长属性数
        String userKey = "userxxx";
        jedis.hset(userKey, "name", "loneranger");
        jedis.hset(userKey, "age", "25");
        jedis.hset(userKey, "phone", "12345678909");
        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hexists(userKey, "phone"));
        print(15, jedis.hvals(userKey));
        jedis.hsetnx(userKey, "school", "nju");
        print(16, jedis.hgetAll(userKey));

        //set:适用于无顺序的集合，点赞点踩，抽奖，已读，共同好友等集合操作
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; i++){
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i*i));
        }
        print(17, jedis.smembers(likeKey1));
        print(18, jedis.smembers(likeKey2));
        print(19, jedis.sunion(likeKey1, likeKey2));
        print(20, jedis.sdiff(likeKey1, likeKey2));
        print(21, jedis.sinter(likeKey1, likeKey2));
        print(22, jedis.sismember(likeKey2, "16"));
        jedis.srem(likeKey2, "16");
        print(23, jedis.smembers(likeKey2));
        jedis.smove(likeKey2, likeKey1, "25");
        print(24, jedis.smembers(likeKey1));
        print(25, jedis.scard(likeKey1));
        //可以使用随机数抽奖，在某个集合里面随机找出count个数
        print(26, jedis.srandmember(likeKey1, 3));


        //sortedset:排行榜，优先队列
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim");
        jedis.zadd(rankKey, 60, "Tom");
        jedis.zadd(rankKey, 70, "Jerry");
        jedis.zadd(rankKey, 80, "Lucy");
        jedis.zadd(rankKey, 90, "James");
        print(27,jedis.zcard(rankKey));
        print(28, jedis.zcount(rankKey, 61, 90));
        print(31, jedis.zscore(rankKey, "Tom"));
        print(31, jedis.zincrby(rankKey, 10, "lucy"));
        print(32, jedis.zrange(rankKey, 1, 3));
        print(32, jedis.zrevrange(rankKey, 1, 3));
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 60, 100)) {
            print(33, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }
        print(34, jedis.zrank(rankKey, "James"));
        print(34, jedis.zrevrank(rankKey, "James"));


        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");
        print(35, jedis.zlexcount(setKey, "-", "+"));
        print(35, jedis.zlexcount(setKey, "(b", "[d"));

        JedisPool pool = new JedisPool();
        for (int i = 0; i < 10; i++) {
            Jedis jedis1 = pool.getResource();
            print(36, jedis1.get("hello"));
            //一定要注意调用close方法释放连接池资源
            jedis1.close();
        }

        User user = new User();
        user.setName("xx");
        user.setPassword("pwd");
        user.setHeadUrl("a.png");
        user.setSalt("salt");
        user.setId(1);
        print(37, JSONObject.toJSONString(user));
        jedis.set("user1", JSONObject.toJSONString(user));

        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value, User.class);
        print(38, user2);
    }


}
