package com.company.toutiao.controller;


import com.company.toutiao.model.User;
import com.company.toutiao.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class IndexController {
    /**
     * AOP:面向切面编程
     */
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    /**
     * Spring的IOC机制，控制反转，依赖注入，可以将写好的Service或者其他类先声明，
     * 在系统启动的时候通过使用Autowired实现依赖注入，找到实现声明为ToutiaoService的类进行绑定，
     * 即可以直接完成实例初始化
     */
    @Autowired
    private ToutiaoService toutiaoService;


    /**
     * 不带参数的网页请求，访问地址为localhost:8080
     * @return
     */
    @RequestMapping(path={"/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String index(HttpSession session){
        logger.info("Visit Index");
        return "Hello World: " + session.getAttribute("msg") + "<br>" + "Say: " + toutiaoService.say();
    }

    /**
     * 带参数的请求，参数请求可以有两种：
     * 1.路径中的参数，可以指定类型
     * 2.请求中的参数，可以设置默认值和类型
     * @param groupId
     * @param userId
     * @param type
     * @param key
     * @return
     */
    @RequestMapping(value={"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("groupId") int groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type", defaultValue = "1") int type,
                          @RequestParam(value = "key", defaultValue = "nowcoder") String key){
        return String.format("{%d}, {%d}, {%d}, {%s}", groupId, userId, type, key);
    }

    /**
     * 使用模板渲染语言FreeMaker渲染传递的参数
     * 使用Model添加各种参数，返回值为对应的模板名称，在模板文件中读取参数进渲染
     * @param model
     * @return
     */
    @RequestMapping(path={"/index"})
    public String news(Model model){
        model.addAttribute("msg", "vv1");
        List<String> colors = Arrays.asList(new String[]{"red", "green", "yellow"});
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 4; i++){
            map.put(String.valueOf(i), String.valueOf(i*i));
        }
        model.addAttribute("colors", colors);
        model.addAttribute("map", map);
        model.addAttribute("user", new User("LoneRanger"));
        return "news";
    }


    /**
     * 对HTTP相关内容进行了解操作，
     * 常用类HttpServletRequest:可以获取收到的request请求内容进行解析查看
     * 包括：参数解析、cookie读取、HTTP请求字段、文件上传
     * HttpServletResponse:可以在response中设置相关参数返回
     * 包括：页面内容返回、cookie下发、HTTP字段设置，headers
     * HttpSession:可以获取一次会话session中的参数，并设置参数
     *
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping(path={"/http"})
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + " : " + request.getHeader(name) + "<br>");
        }

        for (Cookie cookie : request.getCookies()){
            sb.append("Cookie: ");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }

        sb.append("getMethod:" + request.getMethod() + "<br>");
        sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
        sb.append("getQueryString:" + request.getQueryString() + "<br>");

        return sb.toString();
    }

    /**
     * 向cookie中设置传入的nowcoderId
     * @param nowcoderId
     * @param key
     * @param value
     * @param response
     * @return
     */
    @RequestMapping("/response")
    @ResponseBody
    public String response(@CookieValue(value = "nowcoderid", defaultValue = "cookieValue") String nowcoderId,
                           @RequestParam(value = "key", defaultValue = "key") String key,
                           @RequestParam(value = "value", defaultValue = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);
        return "NowCoderId From Cookie: " + nowcoderId;
    }

    /**
     * 重定向操作
     * 301：永久性转移
     * 302：临时性转移
     * @return
     */
    @RequestMapping("/redirect/{code}")
    @ResponseBody
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession session){

        //第一种跳转方式，通过返回一个重定向视图对象，并显示对应的HTTP状态码
        RedirectView red = new RedirectView("/", true);
        if (code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        session.setAttribute("mag", "Jump From Redirect");
        return red;

        //第二种方式，直接重定向回首页,不过返回类型变为String
//              return "redirect:/";
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false) String key){
        if ("admin".equals((key))){
            return "Hello Admin";
        }
        throw new IllegalArgumentException("Key Error");
    }

    /**
     * 自定义ExceptionHandler来处理异常，可以转到错误处理页面
     * @param e
     * @return
     */
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "Error： " + e.getMessage();
    }
}
