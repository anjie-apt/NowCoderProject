package com.company.toutiao.async;

import java.util.List;

public interface EventHandler {

    //涉及到自己关注的event，再调用doHandle方法
    void doHandle(EventModel eventModel);

    //注册自己，表示自己关注哪些event
    List<EventType> getSupportEventTypes();
}
