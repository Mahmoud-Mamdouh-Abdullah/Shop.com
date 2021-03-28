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

    public String getOrderTotal() {
        return orderTotal;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public List<Product> getProductsList() {
        return productsList;
    }
}
