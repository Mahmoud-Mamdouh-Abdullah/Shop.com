package com.mahmoudkhalil.shopcom.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseError;
import com.mahmoudkhalil.shopcom.models.Order;
import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.repo.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel implements OrderRepository.onOrderListener, OrderRepository.onConfirmListener, OrderRepository.onGetOrdersDataListener {
    private OrderRepository orderRepository = new OrderRepository(this, this, this);
    private OrderRepository.onOrderListener onOrderListener;
    private OrderRepository.onConfirmListener onConfirmListener;
    private MutableLiveData<List<Order>> orderMutableLiveData = new MutableLiveData<>();

    public void addOrder(String orderTotal, String userPhone, String orderDate, String userAddress, List<Product> productsList) {
        orderRepository.addOrder(orderTotal, userPhone, orderDate, userAddress, productsList);
    }

    public void confirmOrder(List<Product> confirmedList){
        orderRepository.confirmOrder(confirmedList);
    }

    public void getOrdersData() {
        orderRepository.getAllOrders();
    }

    public MutableLiveData<List<Order>> getOrderMutableLiveData() {
        return orderMutableLiveData;
    }

    public void setOnOrderListener(OrderRepository.onOrderListener onOrderListener) {
        this.onOrderListener = onOrderListener;
    }

    public void setOnConfirmListener(OrderRepository.onConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }


    @Override
    public void onOrderSuccess(Order order) {
        onOrderListener.onOrderSuccess(order);
    }

    @Override
    public void onOrderFailed(Exception e) {
        onOrderListener.onOrderFailed(e);
    }

    @Override
    public void onConfirmSuccess() {
        onConfirmListener.onConfirmSuccess();
    }

    @Override
    public void onConfirmFailed() {
        onConfirmListener.onConfirmFailed();
    }

    @Override
    public void onGetOrdersDataSuccess(List<Order> orderList) {
        orderMutableLiveData.setValue(orderList);
    }
}
