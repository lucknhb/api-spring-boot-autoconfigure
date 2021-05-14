package com.nhb.api.utils;

import cn.hutool.core.util.ReflectUtil;
import com.nhb.api.constant.DataType;
import com.nhb.api.domain.ApiDetailMessage;
import com.nhb.api.domain.HttpRequestType;
import com.nhb.api.domain.Param;
import com.nhb.api.domain.RequestFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author luck_nhb
 * @version 1.0
 * @description 用于获取注解
 * @date 2021/4/28 12:44
 */
public class ApiUtil {
    private static Logger logger = LoggerFactory.getLogger(ApiUtil.class);

    //封装基本类型和参数类型的对应关心
    private static final Map<Class, String> typeMap = new HashMap<>();

    //初始化
    static {
        typeMap.put(byte.class, DataType.NUMBER);
        typeMap.put(short.class, DataType.NUMBER);
        typeMap.put(int.class, DataType.NUMBER);
        typeMap.put(long.class, DataType.NUMBER);
        typeMap.put(float.class, DataType.NUMBER);
        typeMap.put(double.class, DataType.NUMBER);
        typeMap.put(char.class, DataType.STRING);
        typeMap.put(boolean.class, DataType.BOOL);
    }


    /**
     * 获取方法层面请求路径  仅仅是方法层
     *
     * @param method
     */
    public static String getMethodLevelRequestUrl(Method method) {
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        if (null != mapping) {
            return mapping.value()[0];
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return getMapping.value()[0];
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return postMapping.value()[0];
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            return putMapping.value()[0];
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            return deleteMapping.value()[0];
        }
        return "";
    }


    /**
     * 只获取包含@RequestMapping注解的方法
     * 暨可用于请求的方法
     *
     * @param method
     */
    public static boolean filterMethodByRequestMapping(Method method) {
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        if (null != mapping) {
            return true;
        }
        Annotation[] annotations = method.getAnnotations();
        //获取注解上的注解 例如：GetMapping 上 有 RequestMapping
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            RequestMapping requestMappingAnnotation = annotationType.getAnnotation(RequestMapping.class);
            if (requestMappingAnnotation != null) {
                return true;
            }
        }
        return false;
    }



    /**
     * 获取此方法的请求方式
     *
     * @param method
     */
    public static HttpRequestType getRequestType(ApiDetailMessage apiDetailMessage, Method method) {
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        if (null != mapping) {
            judgmentRequestType(apiDetailMessage, method, mapping.method());
            return null;
        }

        Annotation[] annotations = method.getAnnotations();
        //获取请求方式
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            RequestMapping requestMappingAnnotation = annotationType.getAnnotation(RequestMapping.class);
            if (requestMappingAnnotation != null) {
                RequestMethod[] requestMethods = requestMappingAnnotation.method();
                judgmentRequestType(apiDetailMessage, method, requestMethods);
            }
        }
        return null;
    }

    /**
     * 判断请求类型
     *
     * @param apiDetailMessage
     * @param method
     * @param requestMethods
     */
    private static void judgmentRequestType(ApiDetailMessage apiDetailMessage, Method method, RequestMethod[] requestMethods) {
        //默认json格式
        apiDetailMessage.setRequestFormat(RequestFormat.JSON);
        apiDetailMessage.setHttpRequestType(HttpRequestType.ALL);
        if (null != requestMethods && requestMethods.length > 0) {
//            Set<RequestType> requestTypes = new LinkedHashSet<>();
            HttpRequestType requestType = null;
            //TODO 可进行简化 不适用自定义 直接使用Spring web层已有枚举
            for (RequestMethod requestMethod : requestMethods) {
                switch (requestMethod) {
                    case GET:
                        requestType = HttpRequestType.GET;
                        if (containsPathVariableAnnotation(method.getParameterAnnotations())) {
                            apiDetailMessage.setRequestFormat(RequestFormat.RESTFUL);
                        }else {
                            apiDetailMessage.setRequestFormat(RequestFormat.URL);
                        }
                        break;
                    case PUT:
                        requestType = HttpRequestType.PUT;
                        setPutOrPostRequestFormat(apiDetailMessage, method);
                        break;
                    case POST:
                        requestType = HttpRequestType.POST;
                        setPutOrPostRequestFormat(apiDetailMessage, method);
                        break;
                    case DELETE:
                        requestType = HttpRequestType.DELETE;
                        if (containsPathVariableAnnotation(method.getParameterAnnotations())) {
                            apiDetailMessage.setRequestFormat(RequestFormat.RESTFUL);
                        }
                        break;
                    case OPTIONS:
                        requestType = HttpRequestType.OPTIONS;
                        break;
                    case HEAD:
                        requestType = HttpRequestType.HEAD;
                        break;
                    case TRACE:
                        requestType = HttpRequestType.TRACE;
                        break;
                    case PATCH:
                        requestType = HttpRequestType.PATCH;
                        break;
                }
            }
            apiDetailMessage.setHttpRequestType(requestType);
        }
    }

    private static void setPutOrPostRequestFormat(ApiDetailMessage apiDetailMessage, Method method) {
        if (containsPathVariableAnnotation(method.getParameterAnnotations())) {
            apiDetailMessage.setRequestFormat(RequestFormat.RESTFUL);
        } else if (containsRequestBodyAnnotation(method.getParameterAnnotations())){
            apiDetailMessage.setRequestFormat(RequestFormat.JSON);
        }else {
            apiDetailMessage.setRequestFormat(RequestFormat.FORM);
        }
    }


    /**
     * 参数是否使用@PathVariable注解
     *
     * @param parameterAnnotations
     * @return
     */
    private static boolean containsPathVariableAnnotation(Annotation[][] parameterAnnotations) {
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof PathVariable) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 参数是否使用@RequestBody注解
     *
     * @param parameterAnnotations
     * @return
     */
    private static boolean containsRequestBodyAnnotation(Annotation[][] parameterAnnotations) {
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof RequestBody) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 生成/解析请求参数
     *
     * @param method
     * @return
     */
    public static void parseRequestParams(List<Param> requestParams, Method method) {
        //获取参数名 JDK1.8及以上版本
        Parameter[] parameters = method.getParameters();
        /**
         * 获取参数注解 二维数据  第一维度是参数的下标 第二维度是该参数中注解
         * 例如 public void parse(@ApiLabel("xx") Integer param1,String param2)
         * 则 第一维度 为  parameterAnnotations[0] 得到 参数一 param1 中 注解的数量
         * 若无注解则 parameterAnnotations[0] 数量为零
         */
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        //遍历每个参数
        for (int i = 0; i < parameters.length; i++) {
            //获取参数中注解 如无注解则为 0
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            generateParamAndType(requestParams, parameters[i], null, null, null, parameterAnnotation, null);
        }
    }


    /**
     * @param requestParams       最终结果集
     * @param parameter           参数
     * @param type                参数类型
     * @param genType             参数泛型类型
     * @param paramName           参数名称
     * @param parameterAnnotation 针对方法中参数的注解
     * @param classField          针对类中字段(属性)的注解
     */
    private static void generateParamAndType(List<Param> requestParams, Parameter parameter, Class<?> type,
                                             Type genType, String paramName,
                                             Annotation[] parameterAnnotation, Field classField) {
        Param requestParam = new Param();
        requestParam.setId(UUID.randomUUID().toString());
        //设置参数名
        if (null != parameter) {
            //填充参数名
            requestParam.setParamName(parameter.getName());
            //获取参数类型(无泛型)
            type = parameter.getType();
            //获取参数类型(含泛型，例：List<java.lang.String>)
            genType = parameter.getParameterizedType();
            //方法中参数ApiLabel 描述
            requestParam.setDescription(AnnotationUtil.getApiLabelDescriptionForParam(parameterAnnotation));
            //方法中参数ApiLabel 值
            requestParam.setApiLabel(AnnotationUtil.getApiLabelValueForParam(parameter,parameterAnnotation));
        } else if (paramName != null) {
            requestParam.setParamName(paramName);
        } else { //自定义类的参数名
            requestParam.setDescription(AnnotationUtil.getApiLabelDescriptionForClass(type));
            requestParam.setApiLabel(AnnotationUtil.getApiLabelValueForClass(type));
            requestParam.setParamName(type.getSimpleName());
        }
        if (classField != null) {
            //参数对象字段带有ApiLabel  描述
            requestParam.setDescription(AnnotationUtil.getApiLabelDescriptionForField(classField));
            //参数对象字段带有ApiLabel 值
            requestParam.setApiLabel(AnnotationUtil.getApiLabelValueForField(classField));
        }

        //-----------------------------------判断参数类型--------------------------------------------
        judgeParamType(requestParams, type, genType, requestParam);
    }


    /**
     * 判断请求参数类型
     *
     * @param requestParams
     * @param type
     * @param genType
     * @param requestParam
     */
    private static void judgeParamType(List<Param> requestParams, Class<?> type, Type genType, Param requestParam) {
        //字符串
        if (CharSequence.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type) || DataType.STRING.equals(typeMap.get(type))) {
            requestParam.setParamType(DataType.STRING);
            requestParams.add(requestParam);
        }
        //数字 TODO 还可细化
        if (Number.class.isAssignableFrom(type) || DataType.NUMBER.equals(typeMap.get(type))) {
            requestParam.setParamType(DataType.NUMBER);
            requestParams.add(requestParam);
        }
        //布尔
        if (Boolean.class.isAssignableFrom(type) || DataType.BOOL.equals(typeMap.get(type))) {
            requestParam.setParamType(DataType.BOOL);
            requestParams.add(requestParam);
        }
        //文件
        if (InputStreamSource.class.isAssignableFrom(type)) {
            requestParam.setParamType(DataType.FILE);
            requestParams.add(requestParam);
        }
        //自定义对象
        if (ClassUtil.isSelfClass(type)) {
            //TODO  可能存在问题
            requestParam.setParamType(String.format(DataType.SELFOBJECT, type.getSimpleName()));
            requestParams.add(requestParam);
            //自定义对象中字段参数
            List<Param> tempParams = new ArrayList<>();
            requestParam.setChildren(tempParams);
            //获取该类中的字段
            Field[] fields = ReflectUtil.getFieldsDirectly(type, true);
            //static的final修饰的字段
            List<Field> fieldList = removeStaticAndFinal(fields);
            for (Field field : fieldList) {
                Class<?> fieldType = field.getType();
                //自定义类型中可能存在集合泛型
                Type genericType = field.getGenericType();
                generateParamAndType(tempParams, null, fieldType, genericType, field.getName(), null, field);
            }
        }
        //数组
        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            requestParam.setParamType(String.format(DataType.ARRAY,componentType.getSimpleName()));
            requestParams.add(requestParam);
            //多维数组处理
            List<Param> tempParams = new ArrayList<>();
            requestParam.setChildren(tempParams);
            generateParamAndType(tempParams, null, componentType, null, null, null, null);
        }

        //集合含泛型
        if (genType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genType;
            //获取实际泛型
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            //collection
            if (Collection.class.isAssignableFrom(type)) {
                Type actualTypeArgument = actualTypeArguments[0];
                //防止泛型中包含泛型时获取类出错
                String typeName = actualTypeArgument.getTypeName();
                String[] split = typeName.split("<");
                if (split.length > 0) {
                    requestParam.setParamType(String.format(DataType.COLLECTION, DataTypeUtil.split(actualTypeArgument.getTypeName())));
                    requestParams.add(requestParam);
                    List<Param> tempParams = new ArrayList<>();
                    requestParam.setChildren(tempParams);
                    generateParamAndType(tempParams, null, ClassUtil.getClassByName(split[0]), actualTypeArgument, null, null, null);
                }

            } else if (Map.class.isAssignableFrom(type)) {//map
                requestParam.setParamType(String.format(DataType.MAP
                        , DataTypeUtil.split(actualTypeArguments[0].getTypeName()), DataTypeUtil.split(actualTypeArguments[1].getTypeName())));
                requestParams.add(requestParam);
                List<Param> tempParams = new ArrayList<>();
                requestParam.setChildren(tempParams);
                for (Type actualTypeArgument : actualTypeArguments) {
                    String typeName = actualTypeArgument.getTypeName();
                    //需要判断是否泛型中包含泛型
                    String[] split = typeName.split("<");
                    if (split.length > 0) {
                        Class aClass = ClassUtil.getClassByName(split[0]);
                        generateParamAndType(tempParams, null, aClass, actualTypeArgument, null, null, null);
                    } else {
                        generateParamAndType(tempParams, null, ClassUtil.getClassByName(typeName), actualTypeArgument, null, null, null);
                    }
                }
            }
        }
    }

    /**
     * 去掉static的final修饰的字段
     *
     * @param fields 字段列表
     * @return Field[]
     */
    private static List<Field> removeStaticAndFinal(Field[] fields) {
        List<Field> fieldList = new ArrayList<>();
        if (fields.length > 0) {
            for (Field field : fields) {
                String modifier = Modifier.toString(field.getModifiers());
                if (modifier.contains("static") || modifier.contains("final")) {
                    //舍弃
                } else {
                    fieldList.add(field);
                }
            }
        }
        return fieldList;
    }


    /**
     * 解析获取响应参数
     * @param selfMethod
     * @return
     */
    public static List<Param> parseResponseParams(Method selfMethod) {
        Type returnType = selfMethod.getGenericReturnType();
        List<Param> responseParams = new ArrayList<>();
        generateParamAndType(responseParams,null,selfMethod.getReturnType(),returnType,
                null,null,null);
        return responseParams;
    }
}
