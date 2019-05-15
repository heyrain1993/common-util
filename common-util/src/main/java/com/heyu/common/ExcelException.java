package com.heyu.common;

/**
 * 自定义表格异常
 */
public class ExcelException extends RuntimeException {

    public ExcelException(String message){
        super(message);
    }

    public ExcelException(){
        super();
    }

}
