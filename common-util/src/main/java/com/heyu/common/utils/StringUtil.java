package com.heyu.common.utils;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     * @param str
     * @return
     */
    public static Boolean isEmpty(String str){
        if(str == null || "".equals(str)){
            return true;
        }
        return false;
    }
}
