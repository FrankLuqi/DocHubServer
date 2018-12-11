package com.example.dochubserver.async;

import com.alibaba.fastjson.JSON;
import com.example.dochubserver.utils.JedisAdapter;
import com.example.dochubserver.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    public String FireEvent(EventModel eventModel)
    {
        try{
            String json = JSON.toJSONString(eventModel);
            jedisAdapter.lpush(RedisKeyUtil.getEventQueueKey(),json);
            //返回一个uuid作为response 的code，根据code在responses队列中进行查找，找到则返回该response
            eventModel.setResponseCode(UUID.randomUUID().toString().replace("-", ""));
            return eventModel.getResponseCode();
        }catch (Exception e)
        {
            return null;
        }
    }

}
