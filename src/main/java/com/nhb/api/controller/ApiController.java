package com.nhb.api.controller;

import com.nhb.api.domain.ApiDetailMessage;
import com.nhb.api.domain.ApiSimpleMessage;
import com.nhb.api.service.IApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luck_nhb
 * @version 1.0
 * @description 获取Api接口数据
 * @date 2021/4/28 18:27
 */
@RestController
public class ApiController {
    @Autowired
    private IApiService apiService;


    /**
     * 获取api接口简单数据
     *
     * @return
     */
    @GetMapping("/api/getApiSimpleMessages")
    public List<ApiSimpleMessage> getApiSimpleMessages() {
        return apiService.getApiSimpleMessages();
    }


    /**
     * 获取对应方法详细接口数据
     *  TODO 后续完善成根据请求地址匹配返回相应参数
     * @return
     */
    @GetMapping("/api/getApiDetailMessages/{requestUrl}")
    public ApiDetailMessage getApiDetailMessages(@PathVariable String requestUrl) {
        return apiService.getApiDetailMessages(requestUrl);
    }
}
