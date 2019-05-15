package com.heyu.common.enums;

/**
 * Excel文件格式枚举
 */
public enum ExcelType {

    XLS("xls"),
    XLSX("xlsx");

    private String value;

    private ExcelType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
