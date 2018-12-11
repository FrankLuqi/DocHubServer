package com.example.dochubserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool jedisPool;


    @Override
    public void afterPropertiesSet() throws Exception {
//        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxTotal(1024);
//        config.setMaxIdle(10);
//        config.setMaxWaitMillis(1000);
//        config.setTestOnBorrow(true);
//        config.setTestOnReturn(true);

        jedisPool = new JedisPool("redis://120.79.149.135:6379/1");
    }

    public Jedis getJedis()
    {
        return jedisPool.getResource();
    }

    /**
     * 向redis队列中添加元素
     * @param key
     * @param value
     * @return
     */
    public long lpush(String key,String value)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lpush(key,value);
        }catch (Exception e)
        {
            logger.error("向redis队列中添加元素发生异常"+e.getMessage());
        }finally {
            if (jedis!=null)
                jedis.close();
        }
        return 0;
    }

    /**
     * 从redis队列中获取元素，如果队列中没有元素则等待直到时间过期
     * 如果过期时间设为0则一直等待直到有元素
     * @param timeout
     * @param key
     * @return
     */
    public List<String> brpop(int timeout, String key)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.brpop(timeout,key);
        }catch (Exception e)
        {
            logger.error("从redis队列中获取元素发生异常"+e.getMessage());
        }finally {
            if (jedis!=null)
                jedis.close();
        }
        return null;
    }

    /**
     * 获取键值为key的列表长度
     * @param key
     * @return
     */
    public long llen(String key)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.llen(key);
        }catch (Exception e)
        {
            logger.error("从redis队列中获取元素发生异常"+e.getMessage());
        }finally {
            if (jedis!=null)
                jedis.close();
        }
        return -1;
    }

    /**
     * 向哈希表key中添加（field,value)
     * @param key
     * @param field
     * @param value
     * @return
     */

    public long hset(String key,String field,String value)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hset(key,field,value);
        }catch (Exception e)
        {
            logger.error("向redis队列中添加元素发生异常"+e.getMessage());
        }finally {
            if (jedis!=null)
                jedis.close();
        }
        return 0;
    }

    /**
     * 在哈希表key中查找field值的value
     * @param key
     * @param field
     * @return
     */
    public String hget(String key,String field)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hget(key,field);
        }catch (Exception e)
        {
            logger.error("向redis队列中添加元素发生异常"+e.getMessage());
        }finally {
            if (jedis!=null)
                jedis.close();
        }
        return null;
    }

    /**
     * 在哈希表key中查找是否存在field
     * @param key
     * @param field
     * @return
     */
    public boolean hexists(String key,String field)
    {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hexists(key,field);
        }catch (Exception e)
        {
            logger.error("向redis队列中添加元素发生异常"+e.getMessage());
        }finally {
            if (jedis!=null)
                jedis.close();
        }
        return false;
    }
}
