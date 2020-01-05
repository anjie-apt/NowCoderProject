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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    /**
     * 注册逻辑
     * @param model
     * @param userName
     * @param password
     * @param rember
     * @param response
     * @return
     */
    @RequestMapping(path={"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String register(Model model, @RequestParam("username") String userName,
                           @RequestParam("password") String password,
                           @RequestParam(value = "next", required = false) String next,
                           @RequestParam(value = "rember", defaultValue = "0") int rember,
                           HttpServletResponse response){
        /**
         * 调用注册用户服务，返回Map<String, String>用于判断是否发生错误
         * 如果出错，回到注册界面；如果成功，重定向到首页
         */
        try {
            Map<String, String> map = userService.register(userName, password);
            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if (!StringUtils.isEmpty(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("注册异常" + e.getMessage());
            return "login";
        }
    }


    /**
     * /**
     * 登录逻辑：
     * 1.服务器密码校验/三方校验回调，token登记
     *  1.1服务器端token关联userID
     *  1.2客户端存储token(APP存储本地，浏览器存储cookie)
     * 2.服务器/客户端token有效期设置(记住登录)
     *
     * 登出：
     * 1.服务端/客户端token删除
     * 2.session清理
     * @param model
     * @param userName
     * @param password
     * @param rememberme
     * @param response
     * @return
     */
    @RequestMapping(path={"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String userName,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, String> map = userService.login(userName, password);
            //如果用户注册或登录成功，服务器下发一个ticket之后，判断返回的map是否包含该ticket
            //如果包含，则通过response下发一个cookie，否则获取出错信息
            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                //next跳转页面如果不为空，直接跳转到next
                if (!StringUtils.isEmpty(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("登录异常" + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = "/reglogin", method = {RequestMethod.GET})
    public String res(Model model,
                      @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    /**
     * 登出操作，直接调用userService.logout，在DAO中进行数据库更新，
     * 将当前传入的ticket的status置为1，表示登出，返回首页
     * @param ticket
     * @return
     */
    @RequestMapping(path = "/logout", method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }


}
