package com.nhb.autoconfigure.config;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    /**
     * 添加跨域解决的过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        CORSFilter corsFilter = new CORSFilter();
        filterRegistrationBean.setAsyncSupported(true);
        filterRegistrationBean.setFilter(corsFilter);
        List<String> urls = new ArrayList<>();
        urls.add("/*");
        filterRegistrationBean.setUrlPatterns(urls);
        filterRegistrationBean.setOrder(0);
        return filterRegistrationBean;
    }
}
