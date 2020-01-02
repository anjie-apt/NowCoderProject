package com.company.toutiao.service;

import com.company.toutiao.dao.UserDAO;
import com.company.toutiao.model.User;
import com.company.toutiao.utils.TouTiaoUtil;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@Mapper
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public void addUser(User user){
        userDAO.addUser(user);
    }

    public Map<String, Object> register(String username, String password){
        Map<String, Object> res = new HashMap<>();
        //先进行合理性判断
        if (StringUtils.isEmpty(username)){
            res.put("msgname", "用户名不能为空");
        }

        if (StringUtils.isEmpty(password)){
            res.put("msgpwd", "密码不能为空");
        }
        //验证用户是否已经存在，根据用户名查找如果返回了用户对象，则已存在
        User user = userDAO.selectByName(username);
        if (user != null){
            res.put("msgname", "用户名已经被注册");
        }
        //新建用户，设定相关参数，进行盐加密
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt()));
        user.setPassword(TouTiaoUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        //登录

        return res;
    }
}
