package com.example.dochubserver.async;

import javax.servlet.AsyncContext;
import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType eventType;//事件类型
    private Long actorId;//触发事件的用户id
    private Long entityId;//事件要执行操作的实体id
    private EntityType entityType;//要操作的实体的类型，如评论、文章等
    private Long entityOwnerId;//要操作的实体的所有者id
    private AsyncContext asyncContext;//对于异步处理请求时，通过asyncContext获取上下文
    private String ResponseCode;//唯一标识response
//    private String


    private Map<String ,String> parameters = new HashMap<>();

    public String getParameter(String key)
    {
        return parameters.get(key);
    }


    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public Long getActorId() {
        return actorId;
    }

    public EventModel setActorId(Long actorId) {
        this.actorId = actorId;
        return this;
    }

    public Long getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(Long entityId) {
        this.entityId = entityId;
        return this;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(EntityType entityType) {
        this.entityType = entityType;
        return this;
    }

    public Long getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(Long entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public AsyncContext getAsyncContext() {
        return asyncContext;
    }

    public EventModel setAsyncContext(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
        return this;
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }
}
