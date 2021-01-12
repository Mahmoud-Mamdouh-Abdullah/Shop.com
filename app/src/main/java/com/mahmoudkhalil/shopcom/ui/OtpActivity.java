package com.mahmoudkhalil.shopcom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.mahmoudkhalil.shopcom.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OtpActivity extends AppCompatActivity {

    @BindView(R.id.otp_code)
    TextInputLayout otpCode;
    @BindView(R.id.verifyOTP)
    Button verifyOTP;
    private FirebaseAuth mAuth;
    private String OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        OTP = getIntent().getStringExtra("code");
    }

    private void signIn(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
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

    @OnClick(R.id.verifyOTP)
    public void onViewClicked() {
        String verification_code = otpCode.getEditText().getText().toString();
        if (!verification_code.isEmpty() && OTP.equals(verification_code)) {
            sendToChangePassword();
        } else {
            Toast.makeText(OtpActivity.this, "Please enter the OTP Code", Toast.LENGTH_SHORT).show();
            otpCode.setError("Code is incorrect");
        }
    }
}