package com.mahmoudkhalil.shopcom.models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderID;
    private String orderTotal;
    private String userID;
    private String orderDate;
    private String userAddress;
    private List<Product> productsList;

    public Order() {
    }

    public Order(String orderID, String orderTotal, String userID, String orderDate, String userAddress, List<Product> productsList) {
        this.orderID = orderID;
        this.orderTotal = orderTotal;
        this.userID = userID;
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

    public String getUserID() {
        return userID;
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
