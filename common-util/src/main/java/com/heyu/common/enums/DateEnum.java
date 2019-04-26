package com.heyu.common.enums;

public enum DateEnum {

    /**
     * 日期格式化是用y;Y表示当前所在周属于那一年，如果当前周跨年，则格式化日期会发生错误
     */
    FORMAT("yyyyMMdd"),
    FORMAT_DATE("yyyy-MM-dd"),
    FORMAT_TYPE("yyyy/MM/dd"),
    FORMAT_TIME("yyyy-MM-dd HH:mm:ss");

    private String value;

    private DateEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
