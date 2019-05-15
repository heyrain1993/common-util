package com.heyu.common.entity;

import com.heyu.common.annotation.ExcelField;

import java.math.BigDecimal;
import java.util.Date;

public class User {

    @ExcelField(title = "标识")
    private Integer id;

    @ExcelField(title = "用户名")
    private String username;

    @ExcelField(title = "密码")
    private String password;

    @ExcelField(title = "邮箱")
    private String email;

    @ExcelField(title = "年龄")
    private Integer age;

    @ExcelField(title = "存款")
    private BigDecimal money;

    @ExcelField(title = "生日")
    private Date birth;

    @ExcelField(title = "身高")
    private Double height;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
}
