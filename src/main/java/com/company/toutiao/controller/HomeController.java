package com.company.toutiao.controller;

import com.company.toutiao.model.Question;
import com.company.toutiao.model.ViewObject;
import com.company.toutiao.service.QuestionService;
import com.company.toutiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;


    @RequestMapping(path={"/home"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model){
        //读取用户列表
        List<Question> questionList = questionService.getLatestNews(0, 0,10);
        //读取用户头像

        //使用vos进行对象的传递
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList){
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }

        model.addAttribute("vos", vos);
        return "home";
    }
}
