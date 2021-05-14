package com.nhb.api.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author luck_nhb
 * @version 1.0
 * @description  基本参数
 * @date 2021/4/28 17:24
 */
public class Param implements Serializable {
    private String id;
    private String apiLabel;
    private String description;
    //参数名
    private String paramName;
    //参数类型
    private String paramType;

    private List<Param> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Param> getChildren() {
        return children;
    }

    public void setChildren(List<Param> children) {
        this.children = children;
    }

    public String getApiLabel() {
        return apiLabel;
    }

    public void setApiLabel(String apiLabel) {
        this.apiLabel = apiLabel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }
}
