package com.company.toutiao.async.handler;

import com.company.toutiao.async.EventHandler;
import com.company.toutiao.async.EventModel;
import com.company.toutiao.async.EventType;
import com.company.toutiao.model.EntityType;
import com.company.toutiao.model.Message;
import com.company.toutiao.model.User;
import com.company.toutiao.service.MessageService;
import com.company.toutiao.service.UserService;
import com.company.toutiao.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle (EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreateDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        if (eventModel.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName() + "关注了你的问题，链接为http://127.0.0.1:8080/question/" + eventModel.getEntityId());
        }
        else if (eventModel.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户" + user.getName() + "关注了你，链接为http://127.0.0.1:8080/user/" + eventModel.getActorId());
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes () {
        return null;
    }
}
