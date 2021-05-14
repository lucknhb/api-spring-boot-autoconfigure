package com.nhb.api.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author luck_nhb
 * @version 1.0
 * @description  封装具体的请求方法  请求类型  请求数据及其格式  返回数据类型及其格式
 * @date 2021/4/28 17:11
 */
public class ApiDetailMessage implements Serializable {
    //方法注解解析出来的名称 @ApiLabel(value="xxx")
    private String apiLabel;
    private String methodName;
    //控制层类名
    private String className;
    private String description;
    //请求路径
    private String requestUrl;
    //请求类型
    private HttpRequestType httpRequestType;
    //请求参数格式
    private RequestFormat requestFormat;
    //请求参数
    private List<Param> requestParams;
    //响应参数
    private List<Param> responseParams;

    public RequestFormat getRequestFormat() {
        return requestFormat;
    }

    public void setRequestFormat(RequestFormat requestFormat) {
        this.requestFormat = requestFormat;
    }

    public String getApiLabel() {
        return apiLabel;
    }

    public void setApiLabel(String apiLabel) {
        this.apiLabel = apiLabel;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public HttpRequestType getHttpRequestType() {
        return httpRequestType;
    }

    public void setHttpRequestType(HttpRequestType httpRequestType) {
        this.httpRequestType = httpRequestType;
    }

    public List<Param> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<Param> requestParams) {
        this.requestParams = requestParams;
    }

    public List<Param> getResponseParams() {
        return responseParams;
    }

    public void setResponseParams(List<Param> responseParams) {
        this.responseParams = responseParams;
    }
}
