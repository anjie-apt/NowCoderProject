package com.company.toutiao.configuration;


import com.company.toutiao.interceptor.LoginRequestInterceptor;
import com.company.toutiao.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class WendaWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequestInterceptor loginRequestInterceptor;

    /**
     * 注册登录和未登录跳转拦截器，需要注意的是登录注册要在前面先生成hostholder对象
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //需要先添加passport拦截器生成hostholder
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequestInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
