package com.heyu.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heyu.common.entity.User;
import com.heyu.common.enums.ExcelType;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtilTest {

    @Test
    public void test() throws Exception{
        ExcelUtil<User> excelUtil = new ExcelUtil<User>(User.class);
        FileInputStream inputStream = new FileInputStream("C:\\Users\\lenovo\\Desktop\\冲销模板 (1).xlsx");
        List<User> list = excelUtil.importToList(inputStream,"sheet1",1, ExcelType.XLSX);
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void test1() throws Exception {
        User user = JSONObject.parseObject("{\n" +
                "\t\"age\": 18,\n" +
                "\t\"birth\": 1546272000000,\n" +
                "\t\"email\": \"1213@qq.com\",\n" +
                "\t\"height\": 190,\n" +
                "\t\"id\": 1,\n" +
                "\t\"money\": 100.3256,\n" +
                "\t\"password\": \"zhangsan\",\n" +
                "\t\"username\": \"zhangsan\"\n" +
                "}",User.class);
        List<User> users = new ArrayList<User>();
        users.add(user);
        System.out.println(JSON.toJSONString(users));
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\lenovo\\Desktop\\冲销模板 (2).xlsx");
        ExcelUtil<User> excelUtil = new ExcelUtil<User>(User.class);
        excelUtil.exportToExcel(users,"sheet1",fileOutputStream);
    }
}
