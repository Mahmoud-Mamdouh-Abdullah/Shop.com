package com.mahmoudkhalil.shopcom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.databinding.ActivityOtpBinding;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private FirebaseAuth mAuth;
    private String OTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();
        OTP = getIntent().getStringExtra("auth");

        binding.verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verification_code = binding.otpCode.getEditText().getText().toString();
                if(!verification_code.isEmpty()) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, verification_code);
                    signIn(credential);
                } else {
                    Toast.makeText(OtpActivity.this, "Please enter the OTP Code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    sendToChangePassword();
                } else {
                    Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void sendToChangePassword() {
        Intent intent = new Intent(OtpActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
        finish();
    }
}