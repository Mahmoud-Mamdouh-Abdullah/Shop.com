package com.mahmoudkhalil.shopcom.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.User;
import com.mahmoudkhalil.shopcom.repo.UserRepository;
import com.mahmoudkhalil.shopcom.view_model.UserViewModel;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.sign_up_message)
    TextView signUpMessage;
    @BindView(R.id.full_name)
    TextInputLayout fullName;
    @BindView(R.id.email)
    TextInputLayout email;
    @BindView(R.id.password)
    TextInputLayout password;
    @BindView(R.id.confirm_password)
    TextInputLayout confirmPassword;
    @BindView(R.id.phone)
    TextInputLayout phone;
    @BindView(R.id.dateOfBirth)
    TextInputLayout dateOfBirth;
    @BindView(R.id.admin_check)
    CheckBox adminCheck;
    @BindView(R.id.male_radio)
    RadioButton maleRadio;
    @BindView(R.id.female_radio)
    RadioButton femaleRadio;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.signUp)
    Button signUp;
    private String fullNameStr = "", emailStr = "", passwordStr = "", rePass = "", phoneStr = "", gender = "", dateOfBirthStr = "";
    private UserViewModel userViewModel;
    private DatabaseReference mReference;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mReference = FirebaseDatabase.getInstance().getReference("user");

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        dateOfBirth.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showCalendar();
                }
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                dateOfBirthStr = dayOfMonth + "/" + month + "/" + year;
                dateOfBirth.getEditText().setText(dateOfBirthStr);
            }
        };
    }

    private void showCalendar() {
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
        final String phoneNum = user.getPhone();
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(phoneNum).exists()) {
                    phone.setError("this phone number already exists");
                } else {
                    userViewModel.register(user);
                    userViewModel.setOnRegisterListener(new UserRepository.onRegisterListener() {
                        @Override
                        public void onRegisterSuccess() {
                            Toast.makeText(SignUpActivity.this, "Sign up Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        if (fullNameStr.isEmpty())
            fullName.getEditText().setError("Enter your full name!");
        if (emailStr.isEmpty())
            email.getEditText().setError("Enter your Email!");
        if (passwordStr.isEmpty())
            password.setError("Enter your password!");
        if (rePass.isEmpty())
            confirmPassword.setError("Retype your password!");
        if (phoneStr.isEmpty())
            phone.getEditText().setError("Enter your phone number!");
        if (dateOfBirthStr.isEmpty())
            dateOfBirth.getEditText().setError("Enter your date of birth");

    }

    private void init() {
        fullNameStr = fullName.getEditText().getText().toString().trim();
        emailStr = email.getEditText().getText().toString().trim();
        passwordStr = password.getEditText().getText().toString().trim();
        rePass = confirmPassword.getEditText().getText().toString().trim();
        phoneStr = phone.getEditText().getText().toString().trim();
        dateOfBirthStr = dateOfBirth.getEditText().getText().toString();
        if (maleRadio.isChecked()) {
            gender = "male";
        } else if (femaleRadio.isChecked()) {
            gender = "female";
        }

    }

    private boolean validateEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @OnClick(R.id.signUp)
    public void onViewClicked() {
        init();
        errorChecker();
        if (gender.isEmpty() || fullNameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty() || rePass.isEmpty() || phoneStr.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "complete your data", Toast.LENGTH_SHORT).show();
        } else if (!rePass.equals(password)) {
            confirmPassword.setError("password doesn't match!");
        } else if (!validateEmail(emailStr)) {
            email.setError("Invalid email");
        } else {
            String admin = "false";
            if (adminCheck.isChecked())
                admin = "true";
            final User user = new User(admin,
                    fullNameStr,
                    emailStr,
                    passwordStr,
                    phoneStr,
                    gender,
                    dateOfBirthStr);
            password.setError(null);
            confirmPassword.setError(null);
            email.setError(null);
            registration(user);
        }
    }
}