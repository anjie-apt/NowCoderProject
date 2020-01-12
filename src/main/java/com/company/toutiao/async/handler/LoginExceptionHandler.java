package com.company.toutiao.async.handler;

import com.company.toutiao.async.EventHandler;
import com.company.toutiao.async.EventModel;
import com.company.toutiao.async.EventType;
import com.company.toutiao.utils.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle (EventModel eventModel) {
        //XXX判断发现这个用户登录异常
        Map<String, Object> map = new HashMap<>();
        map.put("username", eventModel.getExt("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExt("email"), "登录IP异常", "mails/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes () {
        return Arrays.asList(EventType.LOGIN);
    }
}
