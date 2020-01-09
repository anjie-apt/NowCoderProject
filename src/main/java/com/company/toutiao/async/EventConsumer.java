package com.company.toutiao.async;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware{
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet () throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
//        if (beans != null) {
//            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
//                List<>
//            }
//        }
    }

    @Override
    public void setApplicationContext (ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
