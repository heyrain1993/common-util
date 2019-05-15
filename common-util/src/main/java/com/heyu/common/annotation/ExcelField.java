package com.heyu.common.annotation;

import java.lang.annotation.*;

/**
 * Excel注解工具类
 * 1.Java元注解：@Target:标识注解可用于什么地方.方法、属性。构造器、类局部变量等
 *              @Retention:标识在什么级别保存该信息.SOURCCE 注解在编译时被丢弃;CLASS:注解在class文件保存,但被JVM丢弃;RUNTIME:JVM运行是也会保留,可以通过反射获取注解信息
 *              @Document:将次注解保存在javadoc中
 *              @Inherited:标识允许子类集成父类中的注解
 * 2.Java注解中允许的字段类型：基本数据类型;String;类类型;枚举类型;注解类型;以上类型的数组类型
 * 3.属性权限修饰符:只能使用public、默认权限修饰符
 * 4.常用的方法：<T extends Annotation> T getAnnotation(Class<T> annotationClass) 获取指定类型注解
 */
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelField {

    /**
     * 导出标题
     */
    String title() default "";

    /**
     * 导出导入排序
     */
    int sort() default 0;

    /**
     * 字段类型
     */
    Class<?> fieldType() default Class.class;

    /**
     * 字段最小长度
     */
    int min() default -1;

    /**
     * 字段最小长度
     */
    int max() default -1;

    /**
     * 字段是否允许为空
     */
    boolean isEmpty() default true;

    /**
     * 字段导出时对齐方式 1:居左对齐;2:居中对齐;3:居右对齐
     */
    int align() default 2;

    /**
     * 数据格式化(例如：日期类型:yyyy-MM-dd)
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";
}
