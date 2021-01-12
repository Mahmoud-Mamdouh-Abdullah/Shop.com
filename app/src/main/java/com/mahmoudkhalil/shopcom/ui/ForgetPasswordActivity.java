package com.mahmoudkhalil.shopcom.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.view_model.UserViewModel;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.phone)
    TextInputLayout phoneNum;
    @BindView(R.id.sendOTP)
    Button sendOTP;
    private UserViewModel userViewModel;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mAuth = FirebaseAuth.getInstance();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    private void signIn(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    sendToChangePassword();
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToChangePassword() {
        Intent intent = new Intent(ForgetPasswordActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.sendOTP)
    public void onViewClicked() {
        progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
        progressDialog.show();
        progressDialog.setMessage("Loading....");
        String phone = phoneNum.getEditText().getText().toString();
        if (!phone.isEmpty()) {
            userViewModel.user_exist(phone);
            userViewModel.setOnLoginListener(new UserRepository.onLoginListener() {
                @Override
                public void onLoginSuccess(User user) {
                    String country_code = "+20";
                    String completePhone = country_code + phoneNum.getEditText().getText().toString().trim();

                    PhoneAuthProvider.verifyPhoneNumber(
                            PhoneAuthOptions.newBuilder()
                                    .setPhoneNumber(completePhone)
                                    .setActivity(ForgetPasswordActivity.this)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            String smsCode = phoneAuthCredential.getSmsCode();
                                            if (smsCode != null && !smsCode.isEmpty()) {
                                                Intent intent = new Intent(ForgetPasswordActivity.this, OtpActivity.class);
                                                intent.putExtra("code", smsCode);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(ForgetPasswordActivity.this, "try again later!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).build());
                }

                @Override
                public void onLoginFailure() {
                    phoneNum.setError("This phone is not exist");
                    progressDialog.dismiss();
                }
            });
        }
    }
}