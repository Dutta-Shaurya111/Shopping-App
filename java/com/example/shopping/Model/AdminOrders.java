package com.example.shopping.Model;

public class AdminOrders
{
    private String name,phone,address,city,date,time,totalAmount,shippingState,alternatephoneno;

    AdminOrders(){}

    public AdminOrders(String name, String phone,String alternatephone, String address, String city, String date, String time, String totalAmount, String shippingState) {
        this.name = name;
        this.phone = phone;
        this.alternatephoneno = alternatephone;
        this.address = address;
        this.city = city;
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
        this.shippingState = shippingState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    public void setAlternatephoneno(String alternatephoneno) {
        this.alternatephoneno = alternatephoneno;
    }

    public String getAlternatephoneno() {
        return alternatephoneno;
    }
}
