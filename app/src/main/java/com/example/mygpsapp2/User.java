package com.example.mygpsapp2;

import java.util.List;

public class User {
    private String name;            //用户名
    private String password;        //密码
    private String email;        //邮箱
    private String phonenum;        //手机号码
    private String mostFreCity;
    private String carEge;
    private String carBrand;
    private String carType;
    private String gasType;
    private String purpose;

    public User() {
    }

    User(String name, String password, String email, String phonenum) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phonenum = phonenum;
    }

    public User(String name, String password, String email, String phonenum, String mostFreCity, String carEge, String carBrand, String carType, String gasType, String purpose) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phonenum = phonenum;
        this.mostFreCity = mostFreCity;
        this.carEge = carEge;
        this.carBrand = carBrand;
        this.carType = carType;
        this.gasType = gasType;
        this.purpose = purpose;
    }

    public User(String name, String mostFreCity, String carEge, String carBrand, String carType, String gasType, String purpose) {
        this.name = name;
        this.mostFreCity = mostFreCity;
        this.carEge = carEge;
        this.carBrand = carBrand;
        this.carType = carType;
        this.gasType = gasType;
        this.purpose = purpose;
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public boolean isHaveInfo(){
        if (mostFreCity==null||mostFreCity.isEmpty())
            return false;
        else
            return true;
    }

    public String getMostFreCity() {
        return mostFreCity;
    }

    public void setMostFreCity(String mostFreCity) {
        this.mostFreCity = mostFreCity;
    }

    public String getCarEge() {
        return carEge;
    }

    public void setCarEge(String carEge) {
        this.carEge = carEge;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getGasType() {
        return gasType;
    }

    public void setGasType(String gasType) {
        this.gasType = gasType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phonenum='" + phonenum + '\'' +
                ", mostFreCity='" + mostFreCity + '\'' +
                ", carEge='" + carEge + '\'' +
                ", carBrand='" + carBrand + '\'' +
                ", carType='" + carType + '\'' +
                ", gasType='" + gasType + '\'' +
                ", purpose='" + purpose + '\'' +
                '}';
    }
}