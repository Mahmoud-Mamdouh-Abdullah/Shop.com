package com.mahmoudkhalil.shopcom.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.models.User;

public class UserViewModel extends ViewModel implements UserRepository.onRegisterListener, UserRepository.onLoginListener {
    private UserRepository userRepository = new UserRepository(this,this);
    private UserRepository.onRegisterListener mOnRegisterListener;
    private UserRepository.onLoginListener mOnLoginListener;

    public UserViewModel() {
    }


    public void register(User user) {
        userRepository.register(user);
    }

    public void login(String phone, String password) {
        userRepository.login(phone, password);
    }

    public void user_exist(String phone) {
        userRepository.user_exist(phone);
    }

    public void setOnRegisterListener(UserRepository.onRegisterListener mOnRegisterListener) {
        this.mOnRegisterListener = mOnRegisterListener;
    }

    public void setOnLoginListener(UserRepository.onLoginListener mOnLoginListener) {
        this.mOnLoginListener = mOnLoginListener;
    }

    @Override
    public void onRegisterSuccess() {
        mOnRegisterListener.onRegisterSuccess();
    }

    @Override
    public void onRegisterFailure() {
        mOnRegisterListener.onRegisterFailure();
    }

    @Override
    public void onLoginSuccess(User user) {
        mOnLoginListener.onLoginSuccess(user);
    }

    @Override
    public void onLoginFailure() {
        mOnLoginListener.onLoginFailure();
    }
}
