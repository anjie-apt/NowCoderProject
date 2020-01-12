package com.company.toutiao.async.handler;

import com.company.toutiao.async.EventHandler;
import com.company.toutiao.async.EventModel;
import com.company.toutiao.async.EventType;
import com.company.toutiao.model.Message;
import com.company.toutiao.model.User;
import com.company.toutiao.service.MessageService;
import com.company.toutiao.service.UserService;
import com.company.toutiao.utils.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle (EventModel eventModel) {
        Message message = new Message();
        message.setFromId(WendaUtil.ANONYMOUS_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreateDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的评论，链接为http://127.0.0.1:8080/question/" + eventModel.getExt("questionId"));
    }

    @Override
    public List<EventType> getSupportEventTypes () {
        return null;
    }
}
