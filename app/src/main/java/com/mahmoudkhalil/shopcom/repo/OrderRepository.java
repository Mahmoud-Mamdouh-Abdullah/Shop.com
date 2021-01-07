package com.mahmoudkhalil.shopcom.repo;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahmoudkhalil.shopcom.models.Order;
import com.mahmoudkhalil.shopcom.models.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private onConfirmListener onConfirmListener;
    private onOrderListener onOrderListener;
    private onGetOrdersDataListener onGetOrdersDataListener;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = mDatabase.getReference("orders");
    private DatabaseReference mProductsRef = FirebaseDatabase.getInstance().getReference("Products");

    public OrderRepository(onOrderListener onOrderListener, onConfirmListener onConfirmListener, onGetOrdersDataListener onGetOrdersDataListener) {
        this.onOrderListener = onOrderListener;
        this.onConfirmListener = onConfirmListener;
        this.onGetOrdersDataListener = onGetOrdersDataListener;
    }

    public void addOrder(String orderTotal, String userPhone, String orderDate, String userAddress, List<Product> productsList) {
        String orderID = mRef.push().getKey();
        final Order order = new Order(orderID, orderTotal, userPhone, orderDate, userAddress, productsList);
        mRef.child(orderID).setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onOrderListener.onOrderSuccess(order);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onOrderListener.onOrderFailed(e);
            }
        });
    }

    public void confirmOrder(List<Product> confirmedList) {
        for(int i = 0; i < confirmedList.size(); i ++) {
            mProductsRef.child(confirmedList.get(i).getProduct_code()).setValue(confirmedList.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    onConfirmListener.onConfirmSuccess();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onConfirmListener.onConfirmFailed();
                }
            });
        }
    }

    public void getAllOrders() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Order> orderList = new ArrayList<>();
                for(DataSnapshot orderSnap: snapshot.getChildren()) {
                    Order order = orderSnap.getValue(Order.class);
                    orderList.add(order);
                }
                onGetOrdersDataListener.onGetOrdersDataSuccess(orderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        mRef.addValueEventListener(valueEventListener);
    }

    public interface onOrderListener {
        void onOrderSuccess(Order order);
        void onOrderFailed(Exception e);
    }

    public interface onConfirmListener {
        void onConfirmSuccess();
        void onConfirmFailed();
    }

    public interface onGetOrdersDataListener{
        void onGetOrdersDataSuccess(List<Order> orderList);
    }
}
