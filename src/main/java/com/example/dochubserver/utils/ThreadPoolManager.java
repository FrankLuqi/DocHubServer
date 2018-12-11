package com.example.dochubserver.utils;

import com.alibaba.fastjson.JSON;
import com.example.dochubserver.async.EventHandler;
import com.example.dochubserver.async.EventModel;
import com.example.dochubserver.async.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class ThreadPoolManager {
    // 线程池维护线程的最少数量
    private final static int CORE_POOL_SIZE = 4;
    // 线程池维护线程的最大数量
    private final static int MAX_POOL_SIZE = 4;
    // 线程池维护线程所允许的空闲时间
    private final static int KEEP_ALIVE_TIME = 0;
    // 线程池所使用的缓冲队列大小
    private final static int WORK_QUEUE_SIZE = 100;

    @Autowired
    JedisAdapter jedisAdapter;

    private Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);

    //创建线程池
    private ThreadPoolExecutor threadpool = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.SECONDS,new ArrayBlockingQueue<>(WORK_QUEUE_SIZE));

    //线程池的定时任务----> 称为(调度线程池)。此线程池支持 定时以及周期性执行任务的需求
//    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    /**
     * 每隔1s对redis中的任务队列进行1次查询，并取出任务
     */

    public void init(Map<EventType,List<EventHandler>> config)
    {
        //在这里不采用定时任务的方法，可以直接新建1个线程，在线程中使用while循环（while的判断条件为当前线程池缓冲队列不满），
        //不断对任务队列进行查询，由于redis brpop的特性，如果redis 队列中没有元素，就卡在这里直到有元素，

//        scheduler.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                if (jedisAdapter.llen(RedisKeyUtil.getEventQueueKey())>0)
//                {
//                    List<String> events = jedisAdapter.brpop();
//                }
//            }
//        },0,1,TimeUnit.SECONDS);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    if (threadpool.getQueue().size()<WORK_QUEUE_SIZE)
                    {
                        System.out.println("执行1次");
                        List<String> events = jedisAdapter.brpop(0,RedisKeyUtil.getEventQueueKey());
                        //brpop返回一个list，其中有两个元素，第一个元素是键名，第二个元素是键值
                        for (String event:events)
                        {
                            //忽略第一个元素，键名
                            if (event.equals(RedisKeyUtil.getEventQueueKey()))
                                continue;

                            EventModel eventModel = JSON.parseObject(event,EventModel.class);
                            if (!config.containsKey(eventModel.getEventType()))
                            {
                                logger.error("不能识别的事件类型");
                                continue;
                            }
                            for (EventHandler eventHandler : config.get(eventModel.getEventType()))
                            {
                                eventHandler.doHandle(eventModel);
                            }
                        }
                    }
                }
            }
        });
        thread.start();
    }
}
