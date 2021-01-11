package com.mahmoudkhalil.shopcom.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.view_model.UserViewModel;
import com.mahmoudkhalil.shopcom.databinding.ActivityMainBinding;
import com.mahmoudkhalil.shopcom.models.User;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private Boolean remember;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
        } else {
            binding.signIn.setEnabled(false);
            binding.googleSign.setEnabled(false);
            binding.signUp.setEnabled(false);
            binding.forgetPassword.setEnabled(false);
            View parentView = findViewById(android.R.id.content);
            Snackbar.make(parentView, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Exit", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).setActionTextColor(getResources().getColor(R.color.red)).show();
        }
        gson = new Gson();
        sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        remember = sharedPreferences.getBoolean("remember_user",false);
        if(remember){
            binding.progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();
        }

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.userName.getEditText().getText().toString().trim();
                String pass = binding.password.getEditText().getText().toString().trim();
                userViewModel.login(phone, pass);
                userViewModel.setOnLoginListener(new UserRepository.onLoginListener() {
                    @Override
                    public void onLoginSuccess(User user) {
                        String login_user = gson.toJson(user);
                        editor.putString("login_user", login_user);
                        if(binding.rememberMe.isChecked()){
                            editor.putBoolean("remember_user",true);
                        }
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        MainActivity.this.finish();
                    }

                    @Override
                    public void onLoginFailure() {
                        Toast.makeText(MainActivity.this, "phone or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        binding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgetPasswordActivity.class));
                finish();
            }
        });
    }
}