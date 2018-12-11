package com.example.dochubserver.async;

import com.example.dochubserver.utils.JedisAdapter;
import com.example.dochubserver.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 不断地从事件队列中获取事件并进行处理
 * 实现ApplicationContextAware接口可以获取所有的bean用来获取所有的handler
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    ThreadPoolManager threadPoolManager;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //保存每种事件类型的handler
    private Map<EventType,List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    /**
     * 获取每个eventHandler所能处理的eventType
     * 来初始化所有eventType所需要执行的eventHandler并保存至哈希map config中
     * 之后通过定时运行程序ScheduledThreadPoolExecutor 定时不断从redis事件队列中获取事件
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //获取所有的eventhandler
        Map<String,EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans!=null)
        {
            for (Map.Entry<String,EventHandler> entry:beans.entrySet())
            {
                //获取该handler能处理的所有事件类型
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType eventType : eventTypes)
                {
                    if (!config.containsKey(eventType))
                        config.put(eventType,new ArrayList<>());
                    config.get(eventType).add(entry.getValue());
                }
            }
        }

        //线程池初始化，每隔1s在redis任务队列中查询是否有任务
        threadPoolManager.init(config);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
