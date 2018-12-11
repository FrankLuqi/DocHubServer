package com.example.dochubserver.async;

/**
 * 枚举所有事件类型
 */

public enum  EventType {
    Login(1),
    Register(2);

    private int value;

    EventType(int value)
    {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
