package com.mahmoudkhalil.shopcom.models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderID;
    private String orderTotal;
    private String userPhone;
    private String orderDate;
    private String userAddress;
    private List<Product> productsList;

    public Order() {
    }

    public Order(String orderID, String orderTotal, String userPhone, String orderDate, String userAddress, List<Product> productsList) {
        this.orderID = orderID;
        this.orderTotal = orderTotal;
        this.userPhone = userPhone;
        this.orderDate = orderDate;
        this.userAddress = userAddress;
        this.productsList = productsList;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public List<Product> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Product> productsList) {
        this.productsList = productsList;
    }
}
