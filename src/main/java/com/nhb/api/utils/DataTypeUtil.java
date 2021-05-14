package com.nhb.api.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * @author luck_nhb
 * @version 1.0
 * @description 用于解析参数类型
 * @date 2021/4/28 15:28
 */
public class DataTypeUtil {

    /**
     * 返回该类的简洁名称
     * java.lang.String ---> String
     * java.util.List<java.lang.String> ---> List<String>
     * java.util.Map<java.lang.String,java.util.List<java.lang.String>> ---> Map<String,List<String>>
     * @param
     * @return
     */
    public static String  split(String className){
        if (className.contains("<")){//说明为集合带泛型
            String[] split = className.split("\\.");
            String temp = "";
            for (String s : split) {
                if (s.contains("<")){
                    temp = temp + s.split("<")[0]+"<";
                }else if (s.contains(",")){
                    temp = temp + s.split(",")[0]+",";
                }else if (s.contains(">")){
                    temp = temp+s;
                }
            }
            return temp;
        }else {
            ArrayList<String> list = Lists.newArrayList(Splitter.on('.')
                    .trimResults()
                    .omitEmptyStrings()
                    .split(className));
            return list.get(list.size() - 1 );
        }

    }

}
