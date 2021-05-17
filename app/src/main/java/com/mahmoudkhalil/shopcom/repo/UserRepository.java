package com.mahmoudkhalil.shopcom.repo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mahmoudkhalil.shopcom.models.User;

public class UserRepository {
    private onRegisterListener mOnRegisterListener;
    private onLoginListener mOnLoginListener;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference = mDatabase.getReference("user");


    public UserRepository(onRegisterListener mOnRegisterListener, onLoginListener mOnLoginListener) {
        this.mOnRegisterListener = mOnRegisterListener;
        this.mOnLoginListener = mOnLoginListener;
    }

    public void register(User user) {
        mReference.child(user.getID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(mOnRegisterListener != null) {
                    mOnRegisterListener.onRegisterSuccess();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mOnRegisterListener.onRegisterFailure();
            }
        });
    }

    public void login(final String phone, final String password) {
        Query query = mReference.orderByChild("phone").equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot shot : snapshot.getChildren()) {
                        User user = shot.getValue(User.class);
                        if(user.getPassword().equals(password)) {
                            mOnLoginListener.onLoginSuccess(user);
                        } else {
                            mOnLoginListener.onLoginFailure();
                        }
                    }
                } else {
                    mOnLoginListener.onLoginFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void user_exist(final String phone) {
        Query query = mReference.orderByChild("phone").equalTo(phone);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot shot : snapshot.getChildren()) {
                        User user = shot.getValue(User.class);
                        mOnLoginListener.onLoginSuccess(user);
                    }
                } else {
                    mOnLoginListener.onLoginFailure();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mOnLoginListener.onLoginFailure();
            }
        });
    }

    public  interface onRegisterListener {
        void onRegisterSuccess();
        void onRegisterFailure();
    }
    public  interface onLoginListener {
        void onLoginSuccess(User user);
        void onLoginFailure();
    }

}
