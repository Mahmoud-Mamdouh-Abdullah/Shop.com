package com.mahmoudkhalil.shopcom.repo;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mahmoudkhalil.shopcom.models.Product;
import com.mahmoudkhalil.shopcom.models.User;

import java.util.List;

public class WishCartRepository {
    private onWishListener mOnAddWish;
    private onCartListener onCartListener;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference = mDatabase.getReference("user");

    private DatabaseReference mProductsRef = FirebaseDatabase.getInstance().getReference("Products");

    public WishCartRepository(onWishListener mOnAddWish, onCartListener onCartListener) {
        this.mOnAddWish = mOnAddWish;
        this.onCartListener = onCartListener;
    }

    public void add_to_wish(User user, String productCode) {
        if(!user.wishList.contains(productCode)){
            user.wishList.add(productCode);
            mReference.child(user.getID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(mOnAddWish != null) {
                        mOnAddWish.onWishSuccess();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(mOnAddWish != null) {
                        mOnAddWish.onWishFailure();
                    }
                }
            });
        }

    }

    public void delete_from_wish(User user, String productCode) {
        if(user.wishList.contains(productCode)){
            user.wishList.remove(productCode);
            mReference.child(user.getID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(mOnAddWish != null) {
                        mOnAddWish.onWishSuccess();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(mOnAddWish != null) {
                        mOnAddWish.onWishFailure();
                    }
                }
            });
        }
    }

    public void add_to_cart(User user, String productCode) {
        if(!user.cartList.contains(productCode)){
            user.cartList.add(productCode);
            mReference.child(user.getID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(onCartListener != null) {
                        onCartListener.onCartSuccess();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(onCartListener != null) {
                        onCartListener.onCartSuccess();
                    }
                }
            });
        }
    }

    public void delete_from_cart(User user, String productCode) {
        if(user.cartList.contains(productCode)){
            user.cartList.remove(productCode);
            mReference.child(user.getID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(onCartListener != null) {
                        onCartListener.onCartSuccess();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(onCartListener != null) {
                        onCartListener.onCartFailure();
                    }
                }
            });
        }
    }
    

    public  interface onWishListener {
        void onWishSuccess();
        void onWishFailure();
    }

    public  interface onCartListener {
        void onCartSuccess();
        void onCartFailure();
    }
}
