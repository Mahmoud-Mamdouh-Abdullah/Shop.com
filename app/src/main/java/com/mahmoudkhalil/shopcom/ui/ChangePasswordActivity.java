package com.mahmoudkhalil.shopcom.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.view_model.UserViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.password)
    TextInputLayout password;
    @BindView(R.id.confirm_password)
    TextInputLayout confirmPassword;

    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private User user;
    private String userString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        gson = new Gson();
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userString = getIntent().getStringExtra("user");
        if(userString != null) {
            user = gson.fromJson(userString, User.class);
        }
    }

    @OnClick(R.id.changePassword)
    public void onViewClicked() {
        String passStr = password.getEditText().getText().toString().trim();
        String confirmPassStrt = confirmPassword.getEditText().getText().toString().trim();
        if(!passStr.isEmpty() && !confirmPassStrt.isEmpty() && passStr.equals(confirmPassStrt)) {
            user.setPassword(passStr);
            userViewModel.register(user);
            userViewModel.setOnRegisterListener(new UserRepository.onRegisterListener() {
                @Override
                public void onRegisterSuccess() {
                    String userStr = gson.toJson(user);
                    editor.putString("login_user", userStr);
                    editor.apply();
                    Toast.makeText(ChangePasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }

                @Override
                public void onRegisterFailure() {
                    Toast.makeText(ChangePasswordActivity.this, "Changing password failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}