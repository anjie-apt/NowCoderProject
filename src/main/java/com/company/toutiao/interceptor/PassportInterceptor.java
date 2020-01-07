package com.company.toutiao.interceptor;


import com.company.toutiao.controller.CommentController;
import com.company.toutiao.dao.LoginTicketDAO;
import com.company.toutiao.dao.UserDAO;
import com.company.toutiao.model.LoginTicket;
import com.company.toutiao.model.HostHolder;
import com.company.toutiao.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(PassportInterceptor.class);

    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    HostHolder hostHolder;


    /**
     * 用于在所有的Controller处理前判断用户身份，把用户放到ThreadLocal实现的hostholder中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        //通过遍历request中的cookie，找到服务器分发的ticket
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                if (cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    logger.info("找到ticket");
                    break;
                }
            }
        }

        //根据找到的ticket去数据库中查询，判断登录ticket是否有效（不为空&没有过期&状态码为0）
        if (ticket != null){
            logger.info("ticket: "+ticket);
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            logger.info("loginTicekt: " + loginTicket.getTicket());
            logger.info("ticket对应的用户ID" + loginTicket.getUserId());
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){
                return true;
            }
            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        if (modelAndView != null){
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //结束了清空
        hostHolder.clear();
    }
}
