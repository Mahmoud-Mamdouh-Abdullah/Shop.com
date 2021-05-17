package com.mahmoudkhalil.shopcom.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.view_model.UserViewModel;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.phone)
    EditText phoneNum;
    @BindView(R.id.countryCodePicker)
    CountryCodePicker countryCodePicker;

    private UserViewModel userViewModel;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgetPasswordActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }


    @OnClick(R.id.back_imageButton)
    public void onBackImageButtonClicked() {
        startActivity(new Intent(ForgetPasswordActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @OnClick(R.id.sendOTP)
    public void onSendOTPClicked() {
        progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
        progressDialog.show();
        progressDialog.setMessage("Waiting the verification code ...");
        String phone = phoneNum.getText().toString().trim();
        if (!phone.isEmpty()) {
            userViewModel.user_exist(phone);
            userViewModel.setOnLoginListener(new UserRepository.onLoginListener() {
                @Override
                public void onLoginSuccess(User user) {
                    String country_code = "+" + countryCodePicker.getFullNumber();
                    String completePhone = country_code + phone;
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(completePhone)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(ForgetPasswordActivity.this)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    //if user has to manually enter the code
                                    Intent otpIntent = new Intent(ForgetPasswordActivity.this, OtpActivity.class);
                                    otpIntent.putExtra("code", s);
                                    otpIntent.putExtra("phone", completePhone);
                                    otpIntent.putExtra("user", new Gson().toJson(user));
                                    startActivity(otpIntent);
                                    finish();
                                    Log.i("ForgetPasswordActivity", "onCodeSent");
                                }
                            }).build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }

                @Override
                public void onLoginFailure() {
                    phoneNum.setError("This phone is not exist");
                    progressDialog.dismiss();
                }
            });
        } else {
            phoneNum.setError("Enter your phone number");
        }
    }
}