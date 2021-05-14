package com.nhb.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0
 * @description:  全局统一 API 注解  可作用于 类 方法 参数 属性字段  注意：此注解注解类时，只可注解与控制层
 * @author: luck_nhb
 * @date: 2021/4/28 16:53
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.TYPE,ElementType.METHOD})
public @interface ApiLabel {
    String value() default "";
    String description() default "";
}
