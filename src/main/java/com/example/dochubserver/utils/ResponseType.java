package com.example.dochubserver.utils;

/**
 * 标识该请求的response类型
 */

public enum ResponseType {

    Success(1),
    Error(0);

    private int value;

    ResponseType(int value)
    {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
