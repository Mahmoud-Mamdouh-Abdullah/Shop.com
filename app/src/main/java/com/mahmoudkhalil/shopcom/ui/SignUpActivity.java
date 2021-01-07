package com.mahmoudkhalil.shopcom.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.view_model.UserViewModel;
import com.mahmoudkhalil.shopcom.databinding.ActivitySignUpBinding;
import com.mahmoudkhalil.shopcom.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private String fullName = "", email = "", password = "", rePass = "", phone = "", gender = "", dateOfBirth = "";
    private UserViewModel userViewModel;
    private DatabaseReference mReference;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        mReference = FirebaseDatabase.getInstance().getReference("user");

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
                errorChecker();
                if(gender.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty() || rePass.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "complete your data", Toast.LENGTH_SHORT).show();
                }
                else if(!rePass.equals(password)) {
                    binding.confirmPassword.setError("password doesn't match!");
                }
                else if(!validateEmail(email)) {
                    binding.email.setError("Invalid email");
                }
                else {
                    String admin = "false";
                    if(binding.adminCheck.isChecked())
                        admin = "true";
                    final User user = new User(admin,
                            fullName,
                            email,
                            password,
                            phone,
                            gender,
                            dateOfBirth);
                    binding.password.setError(null);
                    binding.confirmPassword.setError(null);
                    binding.email.setError(null);
                    registration(user);
                }
            }
        });


        binding.dateOfBirth.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showCalendar();
                }
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month ++;
                dateOfBirth = dayOfMonth + "/" + month + "/" + year;
                binding.dateOfBirth.getEditText().setText(dateOfBirth);
            }
        };
    }

    private void showCalendar(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                SignUpActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog_MinWidth, mDateSetListener,
                year, month, day);
        dialog.show();
    }

    private void registration(final User user) {
        final String phone = user.getPhone();
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(phone).exists()) {
                    binding.phone.setError("this phone number already exists");
                }
                else{
                    userViewModel.register(user);
                    userViewModel.setOnRegisterListener(new UserRepository.onRegisterListener() {
                        @Override
                        public void onRegisterSuccess() {
                            Toast.makeText(SignUpActivity.this, "Sign up Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                        @Override
                        public void onRegisterFailure() {
                            Toast.makeText(SignUpActivity.this, "Sign up Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void errorChecker() {
        if(fullName.isEmpty())
            binding.fullName.getEditText().setError("Enter your full name!");
        if(email.isEmpty())
            binding.email.getEditText().setError("Enter your Email!");
        if(password.isEmpty())
            binding.password.setError("Enter your password!");
        if(rePass.isEmpty())
            binding.confirmPassword.setError("Retype your password!");
        if(phone.isEmpty())
            binding.phone.getEditText().setError("Enter your phone number!");
        if(dateOfBirth.isEmpty())
            binding.dateOfBirth.getEditText().setError("Enter your date of birth");

    }

    private void init() {
        fullName = binding.fullName.getEditText().getText().toString().trim();
        email = binding.email.getEditText().getText().toString().trim();
        password = binding.password.getEditText().getText().toString().trim();
        rePass = binding.confirmPassword.getEditText().getText().toString().trim();
        phone = binding.phone.getEditText().getText().toString().trim();
        dateOfBirth = binding.dateOfBirth.getEditText().getText().toString();
        if(binding.maleRadio.isChecked()){
            gender = "male";
        }
        else if(binding.femaleRadio.isChecked()) {
            gender = "female";
        }

    }

    private boolean validateEmail(String email){
        Pattern pattern;
        Matcher matcher;
        String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}