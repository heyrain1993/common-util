package com.heyu.common.utils;

import com.heyu.common.ExcelException;
import com.heyu.common.annotation.ExcelField;
import com.heyu.common.enums.DateEnum;
import com.heyu.common.enums.ExcelType;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * Excel工具类，主要用于导入导出数据
 * 通过反射获取属性或方法上的注解，来导入导出数据
 * 1、自定义注解ExcelField,注解属性包括：字段名、字段标题、字段排序、对齐方式、字段类型
 */
public class ExcelUtil<T> implements Serializable {

    private Class<T> clazz;

    public ExcelUtil(Class<T> clazz){
        this.clazz = clazz;
    }
    /**
     * 导入Excel转换为List
     * @param inputStream 文件流
     * @param sheetName 页签名称
     * @param hearderNo 标题行号
     * @param excelType Excel文件类型
     * @return
     */
    public List<T> importToList(InputStream inputStream,String sheetName,Integer hearderNo,ExcelType excelType) throws IOException, IllegalAccessException, InstantiationException {
        //1.获取Excel页签对象
        Workbook workbook = createWorkBook(inputStream,excelType);
        Sheet sheet = workbook.getSheet(sheetName);
        int rows = sheet.getLastRowNum() + 1;
        if(rows <= 1){
            return new ArrayList<T>();
        }
        //2.获取属性和方法上的注解,组成[注解对象,属性对象]的数组集合
        Field[] fields = clazz.getDeclaredFields();
        List<Object[]> annotationList = new ArrayList<Object[]>();
        for(Field field:fields){
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if(excelField != null){
                annotationList.add(new Object[]{excelField,field});
            }
        }
        /*Method[] methods = clazz.getDeclaredMethods();
        for(Method method:methods){
            ExcelField excelField = method.getAnnotation(ExcelField.class);
            if(excelField != null){
                annotationList.add(new Object[]{excelField,fields});
            }
        }*/
        //3.对集合按照Excel列对象的属性进行排序,为后面一次读取Cell做准备
        Collections.sort(annotationList, new Comparator<Object[]>() {
            public int compare(Object[] annotation1, Object[] annotation2) {
                return new Integer(((ExcelField)annotation1[0]).sort())
                        .compareTo(new Integer(((ExcelField)annotation2[0]).sort()));
            }
        });
        //4.遍历Excel的行数据
        List<T> results = new ArrayList<T>();
        for(int i = hearderNo;i < rows; i++){
            Row row = sheet.getRow(i);
            if(row == null){
                continue;
            }
            T entity = clazz.newInstance();

            //5.遍历每行的单元格,并将单元格中的值设置到对象属性中
            for(int j = 0; j < annotationList.size();j++){
                Object[] annotation = annotationList.get(j);
                ExcelField excelField = (ExcelField) annotation[0];
                Field field = (Field) annotation[1];
                field.setAccessible(true);
                int max = excelField.max();
                int min = excelField.min();
                boolean isEmpty = excelField.isEmpty();
                String dataFormat = excelField.dateFormat();
                Class<?> fieldType = null;
                String cellValueStr = this.getCellValue(row.getCell(j));

                //6.获取属性类型
                fieldType = field.getType();
                /*else if(annotation[1] instanceof Method){
                    Method method = (Method)annotation[1];
                    if(method.getName().startsWith("get")){
                        fieldType = method.getReturnType();
                    }else if(method.getName().startsWith("set")){
                        fieldType = method.getParameterTypes()[0];
                    }
                }*/
                //7.校验空
                if(isEmpty == false && StringUtil.isEmpty(cellValueStr)){
                    throw new ExcelException(excelField.title() + "字段不允许为空");
                }

                if(String.class == fieldType){
                    //8.校验字段长度
                    if(min != -1 && cellValueStr.length() < min){
                        throw new ExcelException(excelField.title() + "字段长度不允许小于" + min);
                    }
                    if(max != -1 && cellValueStr.length() > max){
                        throw new ExcelException(excelField.title() + "字段长度不允许大于" + max);
                    }
                    field.set(entity,cellValueStr);
                }else if (Integer.class == fieldType){
                    field.set(entity,Integer.valueOf(cellValueStr));
                }else if (Double.class == fieldType){
                    field.set(entity,Double.valueOf(cellValueStr));
                }else if (Float.class == fieldType){
                    field.set(entity,Float.valueOf(cellValueStr));
                }else if (Long.class == fieldType){
                    field.set(entity,Long.valueOf(cellValueStr));
                }else if (Date.class == fieldType){
                    Date date = DateUtil.parseStrToDate(cellValueStr,DateEnum.FORMAT_TIME.getValue());
                    field.set(entity,date);
                }else if (BigDecimal.class == fieldType){
                    BigDecimal bigDecimal = new BigDecimal(cellValueStr);
                    field.set(entity,bigDecimal);
                }
            }
            results.add(entity);
        }
        return results;

    }

    private String getCellValue(Cell cell) {
        String value = "";
        if(cell == null){
            return null;
        }

        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){//数值类型：整数、小数、日期
            if(HSSFDateUtil.isCellDateFormatted(cell)){
                Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(cell.getNumericCellValue());
                value = DateUtil.formatDateToStr(date, DateEnum.FORMAT_TIME.getValue());
            }else {//处理科学计数法
                Double num = cell.getNumericCellValue();
                BigDecimal bigDecimal = BigDecimal.valueOf(num);
                if(bigDecimal != null){
                    value = bigDecimal.toPlainString();
                }else {
                    value = BigDecimal.ZERO.toPlainString();
                }
                if(value.endsWith(".0")){
                    value = value.substring(0,value.indexOf("."));
                }
            }
        }else if(cell.getCellType() == Cell.CELL_TYPE_STRING){//字符串
            value = cell.getStringCellValue();
        }else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
            value = String.valueOf(cell.getBooleanCellValue());
        }else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
            value = cell.getCellFormula();
        }
        return value;
    }

    public void exportToExcel(List<T> list, String sheetName, OutputStream outputStream) throws Exception {
        //1.获取所有需要导出的属性和对应的注解对象,组成注解对象,属性对象数据集合
        Field[] fields = clazz.getDeclaredFields();
        List<Object[]> annotationList = new ArrayList<Object[]>();
        for(Field field:fields){
            ExcelField excelField = field.getAnnotation(ExcelField.class);
            if(excelField != null){
                annotationList.add(new Object[]{excelField,field});
            }
        }

        //2.对集合按照注解中的sort字段排序
        Collections.sort(annotationList, new Comparator<Object[]>() {
            public int compare(Object[] annotation1, Object[] annotation2) {
                return new Integer(((ExcelField)annotation1[0]).sort())
                        .compareTo(new Integer(((ExcelField)annotation2[0]).sort()));
            }
        });

        //3.创建Excel对象，HSSFWorkbook,XSSFWorkbook导出数量限制为65535行
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        //4.创建表头,并设置表头的标题
        Row headerRow = sheet.createRow(0);
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);
        for(int i = 0;i < annotationList.size();i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(((ExcelField)annotationList.get(i)[0]).title());
        }

        if(list == null || list.size() == 0){
            throw new ExcelException("导出数据集合为空");
        }
        //5.创建内容行,并设置内容行内容
        for(int i = 0;i < list.size();i++){
            Row row = sheet.createRow(i+1);
            T entity = list.get(i);
            for(int j = 0;j < annotationList.size();j++){
                //6.创建单元格,如果属性为日期类型则作格式化,否则统一转换成字符串导出
                Cell cell = row.createCell(j);
                Field field = (Field) annotationList.get(j)[1];
                field.setAccessible(true);
                ExcelField excelField = (ExcelField) annotationList.get(0)[0];
                Object value = field.get(entity);
                Class<?> fieldType = field.getType();
                if(Date.class == fieldType){
                    String valueDate = DateUtil.formatDateToStr((Date) value,excelField.dateFormat());
                    cell.setCellValue(valueDate);
                }else {
                    cell.setCellValue(field.get(entity) == null?"":String.valueOf(field.get(entity)));
                }
            }
        }
        try {
            outputStream.flush();
            workbook.write(outputStream);
        } finally {
            outputStream.close();
        }
    }

    private Workbook createWorkBook(InputStream inputStream, ExcelType excelType) throws IOException {
        switch (excelType){
            case XLS:
                return new HSSFWorkbook(inputStream);
            case XLSX:
                return new XSSFWorkbook(inputStream);
            default:
                throw new RuntimeException("文档格式不正确!");
        }
    }

}
