package com.mahmoudkhalil.shopcom.repo;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahmoudkhalil.shopcom.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductFirebaseRepository {
    private OnFirebaseComplete onFirebaseComplete;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = mDatabase.getReference("Products");

    public ProductFirebaseRepository(OnFirebaseComplete onFirebaseComplete) {
        this.onFirebaseComplete = onFirebaseComplete;
    }

    public void getProductsData() {

        ValueEventListener productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<Product> productsList = new ArrayList<>();
                for(DataSnapshot productsSnapShpt: snapshot.getChildren()) {
                    Product product = productsSnapShpt.getValue(Product.class);
                    productsList.add(product);
                }
                onFirebaseComplete.productsListDataAdded(productsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mRef.addValueEventListener(productListener);
    }

    public interface OnFirebaseComplete {
        void productsListDataAdded(List<Product> productList);
    }
}
