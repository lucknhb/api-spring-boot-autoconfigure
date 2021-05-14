package com.nhb.api.constant;

/**
 * @author luck_nhb
 * @version 1.0
 * @description  数据类型
 * @date 2021/5/6 14:03
 */
public class DataType {
    // 字符串string 数字number 自定义对象 数组（普通数组，对象数组）
    // 泛型（list map） 文件 boolean 日期Date
    public static final String STRING = "String";
    public static final String NUMBER = "Number";
    public static final String OBJECT = "Object";
    public static final String SELFOBJECT = "SelfObject<%s>";
    public static final String COLLECTION = "Collection<%s>";
    public static final String MAP = "Map<%s,%s>";
    //数组 填充
    public static final String ARRAY = "Array<%s>";
    public static final String BOOL = "Boolean";
    public static final String FILE = "File";
    public static final String DATE = "Date";
}
