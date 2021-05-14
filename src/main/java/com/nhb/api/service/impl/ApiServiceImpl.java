package com.nhb.api.service.impl;

import com.nhb.api.domain.ApiDetailMessage;
import com.nhb.api.domain.ApiSimpleMessage;
import com.nhb.api.domain.Param;
import com.nhb.api.service.IApiService;
import com.nhb.api.utils.AnnotationUtil;
import com.nhb.api.utils.ApiScanUtil;
import com.nhb.api.utils.ApiUtil;
import com.nhb.api.utils.ClassUtil;
import com.nhb.autoconfigure.config.ApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author luck_nhb
 * @version 1.0
 * @description
 * @date 2021/4/28 18:28
 */
@Service
public class ApiServiceImpl implements IApiService {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ApiProperties apiProperties;


    //第一次请求时 将数据填充
    private static Map<String, ApiDetailMessage> apiDetailMessageMap = new HashMap<>();
    private static Map<String, ApiSimpleMessage> apiSimpleMessageMap = new HashMap<>();

    @Override
    public List<ApiSimpleMessage> getApiSimpleMessages() {
        List<ApiSimpleMessage> apiSimpleMessages;
        if (apiSimpleMessageMap.size() > 0) {
            Collection<ApiSimpleMessage> values = apiSimpleMessageMap.values();
            apiSimpleMessages = new ArrayList<>(values);
            return apiSimpleMessages;
        }

        //根据配置的包扫描路径获取被 ApiLabel && (Controller || RestController)注解的类
        Set<Class> apiLabelClass = ApiScanUtil.getApiLabelAnnotation(apiProperties.getScanPackage());
        apiSimpleMessages = getApiSimpleMessages(apiLabelClass);
        //将数据填充到内存中
        new Thread(() -> {
            for (ApiSimpleMessage apiSimpleMessage : apiSimpleMessages) {
                apiSimpleMessageMap.put(apiSimpleMessage.getClassName(), apiSimpleMessage);
            }
        }).start();
        return apiSimpleMessages;
    }

    /**
     * 根据类名 方法名获取到具体的请求数据 响应数据
     *
     * @return
     */
    @Override
    public ApiDetailMessage getApiDetailMessages(String requestUrl) {
        ApiDetailMessage detailMessage = apiDetailMessageMap.get(requestUrl);
        if (Objects.nonNull(detailMessage)) {
            return detailMessage;
        }

        String[] split = requestUrl.split("@");
        String className = split[0];
        String methodName = split[1];
        //获取到对应的类
        Class aClass = ClassUtil.getClassByName(className);
        if (Objects.nonNull(aClass)) {
            //从类中获取对应的方法
            List<Method> selfMethods = ClassUtil.getSelfMethods(aClass);
            for (Method selfMethod : selfMethods) {
                if (ApiUtil.filterMethodByRequestMapping(selfMethod)) {
                    //匹配到方法
                    if (methodName.equals(selfMethod.getName())) {
                        ApiDetailMessage apiDetailMessage = new ApiDetailMessage();
                        //获取http请求方式 以及请求格式
                        ApiUtil.getRequestType(apiDetailMessage, selfMethod);
                        //获取基本数据
                        apiDetailMessage.setApiLabel(AnnotationUtil.getApiLabelValueForMethod(selfMethod));
                        apiDetailMessage.setDescription(AnnotationUtil.getApiLabelDescriptionForMethod(selfMethod));
                        apiDetailMessage.setClassName(className);
                        apiDetailMessage.setMethodName(selfMethod.getName());
                        apiDetailMessage.setRequestUrl(ClassUtil.getControlLevelRequestUrl(aClass) + ApiUtil.getMethodLevelRequestUrl(selfMethod));
                        //请求参数
                        apiDetailMessage.setRequestParams(getRequestParams(selfMethod));
                        //响应参数
                        apiDetailMessage.setResponseParams(getResponseParams(selfMethod));

                        //填充至内存
                        apiDetailMessageMap.put(requestUrl, apiDetailMessage);
                        return apiDetailMessage;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取响应参数
     *
     * @param selfMethod
     * @return
     */
    private List<Param> getResponseParams(Method selfMethod) {
        List<Param> responseParams = ApiUtil.parseResponseParams(selfMethod);

        return responseParams;
    }


    /**
     * 根据每个控制层获取请求方法
     *
     * @param classes
     * @return
     */
    private List<ApiSimpleMessage> getApiSimpleMessages(Set<Class> classes) {
        List<ApiSimpleMessage> apiSimpleMessages = new ArrayList<>();
        for (Class aClass : classes) {
            //获取类中所有public方法
            List<Method> methodList = ClassUtil.getSelfMethods(aClass);
            //控制层数据
            ApiSimpleMessage controlLevelMessage = new ApiSimpleMessage();
            List<ApiSimpleMessage> children = new ArrayList<>();
            controlLevelMessage.setChildren(children);
            //类名
            controlLevelMessage.setClassName(aClass.getName());
            //ApiLabel注解值
            controlLevelMessage.setApiLabel(AnnotationUtil.getApiLabelValueForClass(aClass));
            //获取描述
            controlLevelMessage.setDescription(AnnotationUtil.getApiLabelDescriptionForClass(aClass));
            //获取请求路径(若有)
            controlLevelMessage.setRequestUrl(ClassUtil.getControlLevelRequestUrl(aClass));
            apiSimpleMessages.add(controlLevelMessage);
            for (Method method : methodList) {
                //过滤出来可请求的方法
                if (ApiUtil.filterMethodByRequestMapping(method)) {
                    //开始封装方法层数据
                    ApiSimpleMessage methodLevelMessage = new ApiSimpleMessage();
                    //获取请求地址
                    methodLevelMessage.setRequestUrl(controlLevelMessage.getRequestUrl() + ApiUtil.getMethodLevelRequestUrl(method));
                    methodLevelMessage.setDescription(AnnotationUtil.getApiLabelDescriptionForMethod(method));
                    methodLevelMessage.setClassName(controlLevelMessage.getClassName());
                    methodLevelMessage.setApiLabel(AnnotationUtil.getApiLabelValueForMethod(method));
                    methodLevelMessage.setMethodName(method.getName());
                    //将方法数据添加到控制层中的子节点中
                    children.add(methodLevelMessage);
                }
            }
            controlLevelMessage.setMethodCount(children.size());
        }
        return apiSimpleMessages;
    }


    /**
     * 获取该方法的请求参数
     *
     * @param method
     * @return
     */
    private List<Param> getRequestParams(Method method) {
        List<Param> requestParams = new ArrayList<>();
        ApiUtil.parseRequestParams(requestParams, method);
        return requestParams;
    }


}
