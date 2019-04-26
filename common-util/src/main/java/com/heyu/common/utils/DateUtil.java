package com.heyu.common.utils;

import com.heyu.common.enums.DateEnum;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 常用日期工具类
 */
public class DateUtil {

    private static final Integer FORMAT_SIZE = 3;

    /**
     * 1.ThreadLocal作用：ThreadLocal为变量在每个线程中都创建了一个副本，各线程之前互不干扰。
     * 2.在使用ThreadLocal对象之前必须先调用set()方法，才能调用get()方法，否则回报空指针异常。
     * 3.在调用ThreadLocal的get()方法时，如下所示，是获取当前线程对象中的ThreadLocalMap中的value;
     *   如果当前线程的ThreadLocalMap对象为null，则调用setInitialValue()方法初始化当前线程对象的ThreadLocalMap对象，
     *   并以ThreadLocal对象为key，对应的共享变量为value
     * 4.ThreadLocal有两种初始化方法：1.重写initialValue()方法；2.调用set()方法初始化
     */

    /*public T get() {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }*/

    /*private T setInitialValue() {
        T value = initialValue();
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
        return value;
    }*/

    /*public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }*/

    private static ThreadLocal<Map<String,DateFormat>> threadLocal = new ThreadLocal<Map<String,DateFormat>>();

    /**
     * 字符串转换为日期
     * @param dateStr 日期字符串
     * @param dateEnum 字符串格式
     * @return
     */
    public static Date parseStrToDate(String dateStr, DateEnum dateEnum) {
        if(StringUtil.isEmpty(dateStr)){
            return null;
        }
        try {
            return getDateFormat(dateEnum).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 日期格式化为字符串
     * @param date 日期
     * @param dateEnum 字符串格式
     * @return
     */
    public static String formatDateToStr(Date date,DateEnum dateEnum){
        return getDateFormat(dateEnum).format(date);
    }

    /**
     * 获取对应格式的DateFormat.SimpleDateFormat是线程不安全的原因：SimpleDateFormat类的父类DateFormat中包含成员属性Calendar,在多线程场景下，
     * 会发生资源共享，导致前后不一致问题。例如：calender.setTime(Date date),这句会导致后面的线程B把前面线程A设置的日期改成自己的,
     * 导致线程A的日期错误
     * @param dateEnum 字符串格式
     * @return
     */
    private static DateFormat getDateFormat(DateEnum dateEnum) {
        Map<String,DateFormat> map = threadLocal.get();
        DateFormat dateFormat = null;
        if (map == null){
            map = new HashMap<String, DateFormat>(FORMAT_SIZE);
            dateFormat = new SimpleDateFormat(dateEnum.getValue());
            map.put(dateEnum.getValue(),dateFormat);
            threadLocal.set(map);
        }else if(map.get(dateEnum.getValue()) == null){
            dateFormat = new SimpleDateFormat(dateEnum.getValue());
            map.put(dateEnum.getValue(),dateFormat);
            threadLocal.set(map);
        }else {
            dateFormat = map.get(dateEnum.getValue());
        }
        return dateFormat;
    }
}
