package com.mahmoudkhalil.shopcom.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.Logic;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.view_model.UserViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.user_name)
    TextInputLayout userName;
    @BindView(R.id.password)
    TextInputLayout password;
    @BindView(R.id.remember_me)
    CheckBox rememberMe;
    @BindView(R.id.google_sign)
    Button googleSign;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.forget_password)
    TextView forgetPassword;
    @BindView(R.id.signIn)
    Button signIn;
    @BindView(R.id.sign_up)
    TextView signUp;
    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (!Logic.isNetworkConnected(this)) {
            disableViews();
            showSnackbar();
        }
        gson = new Gson();
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Boolean remember = sharedPreferences.getBoolean("remember_user", false);
        if (remember) {
            progressBar.setVisibility(View.VISIBLE);
            toHomeActivity();
        }
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @OnClick(R.id.forget_password)
    public void onForgetPasswordClicked() {
        startActivity(new Intent(MainActivity.this, ForgetPasswordActivity.class));
        finish();
    }

    @OnClick(R.id.signIn)
    public void onSignInClicked() {
        String phone = userName.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();
        userViewModel.login(phone, pass);
        userViewModel.setOnLoginListener(new UserRepository.onLoginListener() {
            @Override
            public void onLoginSuccess(User user) {
                String login_user = gson.toJson(user);
                editor.putString("login_user", login_user);
                if (rememberMe.isChecked()) {
                    editor.putBoolean("remember_user", true);
                }
                editor.apply();
                toHomeActivity();
            }

            @Override
            public void onLoginFailure() {
                Toast.makeText(MainActivity.this, "phone or password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.sign_up)
    public void onSignUpClicked() {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void disableViews() {
        signIn.setEnabled(false);
        googleSign.setEnabled(false);
        signUp.setEnabled(false);
        forgetPassword.setEnabled(false);
    }
    private void showSnackbar() {
        View parentView = findViewById(android.R.id.content);
        Snackbar.make(parentView, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Exit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).setActionTextColor(getResources().getColor(R.color.red)).show();
    }

    private void toHomeActivity(){
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("from", "main");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.exit_dialog);
        Button ok = dialog.findViewById(R.id.ok);
        Button cancel = dialog.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}