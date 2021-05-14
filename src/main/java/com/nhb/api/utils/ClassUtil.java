package com.nhb.api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author luck_nhb
 * @version 1.0
 * @description  类工具类
 * @date 2021/5/6 19:14
 */
public class ClassUtil {
    private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 根据类全名获得类对象
     *
     * @param className 类全名
     * @return Class
     */
    public static Class getClassByName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("获取{}类发生异常:{}", className, e.getMessage());
        }
        return null;
    }


    /**
     * 是否是自定义类型
     *
     * @param clz class对象
     * @return boolean
     */
    public static boolean isSelfClass(Class<?> clz) {
        if (clz == null) {
            return false;
        }
        //排除 spring的文件类型
        if (MultipartFile.class.isAssignableFrom(clz)) {
            return false;
        }
        //排除数组
        if (clz.isArray()) {
            return false;
        }
        //Object 类型特殊处理
        if (clz == Object.class) {
            return true;
        }
        //只能是jdk的根加载器
        return clz.getClassLoader() != null;
    }

    /**
     * 获取控制层中请求路径
     *
     * @param aClass
     * @return
     */
    public static String getControlLevelRequestUrl(Class aClass) {
        //控制层只有RequestMapping可以进行注解 故只进行解析一个
        RequestMapping mapping = (RequestMapping) aClass.getDeclaredAnnotation(RequestMapping.class);
        if (null != mapping) {
            return mapping.value()[0];
        }
        return "";
    }



    /**
     * 获取自身非继承的方法
     *
     * @param aClass
     * @return
     */
    public static List<Method> getSelfMethods(Class aClass) {
        Method[] methods = aClass.getMethods();
        //获取父类以及父类中的方法
        Class superclass = aClass.getSuperclass();
        Method[] superclassMethods = superclass.getMethods();
        //排除wait/notify等父类方法
        List<Method> methodList = new LinkedList<>(Arrays.asList(methods));
        methodList.removeAll(new LinkedHashSet<>(Arrays.asList(superclassMethods)));
        return methodList;
    }


}
