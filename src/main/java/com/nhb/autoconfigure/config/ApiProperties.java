package com.nhb.autoconfigure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author luck_nhb
 * @version 1.0
 * @description
 * @date 2021/5/14 11:15
 */
@ConfigurationProperties(prefix = "com.nhb.api")
public class ApiProperties {
    private boolean enable = true;
    private String scanPackage;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }
}
