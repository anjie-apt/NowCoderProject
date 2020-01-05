package com.company.toutiao.service;

import com.company.toutiao.dao.LoginTicketDAO;
import com.company.toutiao.dao.UserDAO;
import com.company.toutiao.model.LoginTicket;
import com.company.toutiao.model.User;
import com.company.toutiao.utils.WendaUtil;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Mapper
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public void addUser(User user){
        userDAO.addUser(user);
    }

    public Map<String, String> register(String username, String password){
        Map<String, String> res = new HashMap<>();
        //先进行合理性判断
        if (StringUtils.isEmpty(username)){
            res.put("msg", "用户名不能为空");
        }

        if (StringUtils.isEmpty(password)){
            res.put("msg", "密码不能为空");
        }
        //验证用户是否已经存在，根据用户名查找如果返回了用户对象，则已存在
        User user = userDAO.selectByName(username);
        if (user != null){
            res.put("msg", "用户名已经被注册");
        }
        //新建用户，设定相关参数，进行盐加密
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt()));
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);
        logger.info("用户注册成功！");

        //登陆或注册完之后，需要给用户下发一个关联登陆状态和userID的ticket
        String ticket =  addLoginTicket(user.getId());
        res.put("ticket", ticket);
        logger.info("下发ticket成功，即将跳转至首页");
        return res;
    }

    public Map<String,String> login(String username, String password) {
        Map<String, String> res = new HashMap<>();
        //先进行合理性判断
        if (StringUtils.isEmpty(username)){
            res.put("msg", "用户名不能为空");
        }

        if (StringUtils.isEmpty(password)){
            res.put("msg", "密码不能为空");
        }
        //验证用户是否已经存在，根据用户名查找,如果没有返回用户对象，则用户名不存在
        User user = userDAO.selectByName(username);
        if (user == null){
            res.put("msg", "用户名不存在");
        }
        //如果用户存在，进行加密验证，使用和MD5把输入的密码和对应的盐值进行加密，判断是否等于对应的用户密码
        if (!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            res.put("msg", "密码错误");
            return res;
        }
        logger.info("用户登录成功！");

        //登陆或注册完之后，需要给用户下发一个关联登陆状态和userID的ticket
        String ticket =  addLoginTicket(user.getId());
        res.put("ticket", ticket);
        logger.info("下发ticket成功，即将跳转至首页");
        return res;
    }

    public String addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600*24*100 + now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }
}
