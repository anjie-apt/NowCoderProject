package com.company.toutiao.controller;


import com.company.toutiao.model.News;
import com.company.toutiao.model.ViewObject;
import com.company.toutiao.service.NewsService;
import com.company.toutiao.service.UserService;
import com.company.toutiao.utils.TouTiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @RequestMapping(path={"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String register(Model model, @RequestParam("username") String userName,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int rember){

        try {
            Map<String, Object> map = userService.register(userName, password);
            if (map.isEmpty()){
                return TouTiaoUtil.getJSONString(0, "注册成功");
            }else {
                return TouTiaoUtil.getJSONString(1, map);
            }
        }catch (Exception e){
            logger.error("注册异常" + e.getMessage());
            return TouTiaoUtil.getJSONString(1, "注册异常");
        }
    }




}
