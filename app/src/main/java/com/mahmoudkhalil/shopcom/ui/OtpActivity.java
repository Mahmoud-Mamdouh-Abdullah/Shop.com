package com.mahmoudkhalil.shopcom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mahmoudkhalil.shopcom.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OtpActivity extends AppCompatActivity {


    @BindView(R.id.verifyOTP)
    Button verifyOTP;
    @BindView(R.id.pinViewCode)
    PinView pinViewCode;
    @BindView(R.id.otp_desc_tv)
    TextView otpDescTv;
    private FirebaseAuth mAuth;
    private String OTP, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        OTP = getIntent().getStringExtra("code");
        phone = getIntent().getStringExtra("phone");
        otpDescTv.setText(String.format("%s%s", getString(R.string.otp_description), phone));
    }

    private void sendToChangePassword() {
        Intent intent = new Intent(OtpActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
        OtpActivity.this.finish();
    }

    @OnClick(R.id.verifyOTP)
    public void onViewClicked() {
        String verification_code = pinViewCode.getText().toString();
        if(!verification_code.isEmpty()) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTP, verification_code);
            signIn(credential);
        } else {
            Toast.makeText(OtpActivity.this, "Please enter the OTP Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    sendToChangePassword();
                } else {
                    Toast.makeText(OtpActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}