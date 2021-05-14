package com.nhb.api.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author luck_nhb
 * @version 1.0
 * @description api接口解析封装类 止步于 method  仅仅显示控制层标签名 以及 里面方法标签名
 * @date 2021/4/28 14:33
 */
public class ApiSimpleMessage implements Serializable {
    //控制层类中解析出来的名称 @ApiLabel(value="xxx")
    private String apiLabel;
    //该层次拼装的请求路径  控制层-->xxx  方法-->xxx/yyy
    private String requestUrl;
    //控制层类名
    private String className;
    //用于方法层
    private String methodName;
    //控制层类中解析出来的名称 @ApiLabel(description="xxx")
    private String description;
    //具体请求的方法信息
    private List<ApiSimpleMessage> children;
    //该controller中方法总数
    private Integer methodCount;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getApiLabel() {
        return apiLabel;
    }

    public void setApiLabel(String apiLabel) {
        this.apiLabel = apiLabel;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getMethodCount() {
        return methodCount;
    }

    public void setMethodCount(Integer methodCount) {
        this.methodCount = methodCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ApiSimpleMessage> getChildren() {
        return children;
    }

    public void setChildren(List<ApiSimpleMessage> children) {
        this.children = children;
    }
}
