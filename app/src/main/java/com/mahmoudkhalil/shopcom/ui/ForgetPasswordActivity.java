package com.mahmoudkhalil.shopcom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.databinding.ActivityForgetPasswordBinding;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.view_model.UserViewModel;

import java.util.concurrent.TimeUnit;

public class ForgetPasswordActivity extends AppCompatActivity {

    private ActivityForgetPasswordBinding binding;
    private UserViewModel userViewModel;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password);
        mAuth = FirebaseAuth.getInstance();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
                progressDialog.show();
                progressDialog.setMessage("Loading....");
                String phone = binding.phone.getEditText().getText().toString();
                if(!phone.isEmpty()) {
                    userViewModel.user_exist(phone);
                    userViewModel.setOnLoginListener(new UserRepository.onLoginListener() {
                        @Override
                        public void onLoginSuccess(User user) {
                            String country_code = "+2";
                            String completePhone = country_code + user.getPhone().trim();
                            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(completePhone)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(ForgetPasswordActivity.this)
                                    .setCallbacks(mCallBacks)
                                    .build();
                            PhoneAuthProvider.verifyPhoneNumber(options);
                        }

                        @Override
                        public void onLoginFailure() {
                            binding.phone.setError("This phone is not exist");
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(ForgetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull final String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ForgetPasswordActivity.this, OtpActivity.class);
                        intent.putExtra("auth", s);
                        startActivity(intent);
                    }
                }, 10000);

            }

        };
    }

    private void signIn(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
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
}