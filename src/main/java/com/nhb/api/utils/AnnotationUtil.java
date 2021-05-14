package com.nhb.api.utils;

import cn.hutool.core.util.StrUtil;
import com.nhb.api.annotation.ApiLabel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * @author luck_nhb
 * @version 1.0
 * @description 用于获取ApiLabel中的数据
 * @date 2021/4/30 10:34
 */
public class AnnotationUtil {


    /**
     * 获取类上ApiLabel注解值 value
     *
     * @param aClass
     * @return
     */
    public static String getApiLabelValueForClass(Class aClass) {
        ApiLabel apiLabel = (ApiLabel) aClass.getAnnotation(ApiLabel.class);
        if (Objects.nonNull(apiLabel)) {
            return StrUtil.isBlank(apiLabel.value()) ? aClass.getName() : apiLabel.value();
        }
        return aClass.getSimpleName();
    }

    /**
     * 获取类上ApiLabel注解值 description
     *
     * @param aClass
     * @return
     */
    public static String getApiLabelDescriptionForClass(Class aClass) {
        ApiLabel apiLabel = (ApiLabel) aClass.getAnnotation(ApiLabel.class);
        if (Objects.nonNull(apiLabel)) {
            return StrUtil.isBlank(apiLabel.description()) ? "暂无功能描述" : apiLabel.description();
        }
        return "暂无功能描述";
    }


    /**
     * 获取方法上ApiLabel注解值 value
     *
     * @param method
     * @return
     */
    public static String getApiLabelValueForMethod(Method method) {
        ApiLabel apiLabel = method.getAnnotation(ApiLabel.class);
        if (Objects.nonNull(apiLabel)) {
            return StrUtil.isBlank(apiLabel.value()) ? method.getName() : apiLabel.value();
        }
        return method.getName();
    }


    /**
     * 获取方法上ApiLabel注解值 description
     *
     * @param method
     * @return
     */
    public static String getApiLabelDescriptionForMethod(Method method) {
        ApiLabel apiLabel = method.getAnnotation(ApiLabel.class);
        if (Objects.nonNull(apiLabel)) {
            return StrUtil.isBlank(apiLabel.description()) ? "暂无功能描述" : apiLabel.description();
        }
        return "暂无功能描述";
    }


    /**
     * 获取方法中参数的注解 value值
     *
     * @param annotations
     * @return
     */
    public static String getApiLabelValueForParam(Parameter parameter, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ApiLabel) {
                ApiLabel apiLabel = (ApiLabel) annotation;
                return Objects.isNull(apiLabel) && StrUtil.isBlank(apiLabel.value()) ? parameter.getName() : apiLabel.value();
            }
        }
        return parameter.getName();
    }


    /**
     * 针对方法中的参数 description 值
     *
     * @param annotations
     * @return
     */
    public static String getApiLabelDescriptionForParam(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ApiLabel) {
                ApiLabel apiLabel = (ApiLabel) annotation;
                return Objects.isNull(apiLabel) && StrUtil.isBlank(apiLabel.description()) ? "暂无描述" : apiLabel.description();
            }
        }
        return "暂无描述";
    }

    /**
     * 针对类中字段/属性 中注解 description
     *
     * @param field
     * @return
     */
    public static String getApiLabelDescriptionForField(Field field) {
        ApiLabel apiLabel = field.getAnnotation(ApiLabel.class);
        return Objects.nonNull(apiLabel) && StrUtil.isNotBlank(apiLabel.description()) ? apiLabel.description() : field.getName();
    }

    /**
     * 针对类中字段/属性 中注解 value
     *
     * @param field
     * @return
     */
    public static String getApiLabelValueForField(Field field) {
        ApiLabel apiLabel = field.getAnnotation(ApiLabel.class);
        return Objects.nonNull(apiLabel) && StrUtil.isNotBlank(apiLabel.value()) ? apiLabel.value() : field.getName();
    }


}
