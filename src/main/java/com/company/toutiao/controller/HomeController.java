package com.company.toutiao.controller;

import com.company.toutiao.model.News;
import com.company.toutiao.model.ViewObject;
import com.company.toutiao.service.NewsService;
import com.company.toutiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class HomeController {
    @Autowired
    private NewsService newsService;

    @Autowired
    private UserService userService;


    @RequestMapping(path={"/home"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model){
        //读取用户列表
        List<News> newsList = newsService.getLatestNews(0, 0,10);
        //读取用户头像

        //使用vos进行对象的传递
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList){
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            vos.add(vo);
        }

        model.addAttribute("vos", vos);
        return "home";
    }
}
