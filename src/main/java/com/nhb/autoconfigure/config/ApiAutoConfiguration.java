package com.nhb.autoconfigure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author luck_nhb
 * @version 1.0
 * @description
 * @date 2021/5/14 11:18
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(ApiProperties.class)
@ComponentScan("com.nhb.*")
@ConditionalOnProperty(value = "com.nhb.api.enable",havingValue = "true")
public class ApiAutoConfiguration  {


    @Autowired
    private ApiProperties apiProperties;

}
