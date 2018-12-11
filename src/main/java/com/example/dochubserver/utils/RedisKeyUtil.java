package com.example.dochubserver.utils;

import org.springframework.stereotype.Service;

public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }
}
