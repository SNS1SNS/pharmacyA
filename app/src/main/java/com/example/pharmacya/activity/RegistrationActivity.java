package com.example.pharmacya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmacya.R;
import com.example.pharmacya.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import android.text.InputType;
public class RegistrationActivity extends AppCompatActivity {
    Button signUp;
    EditText name, email, password, phone, address;
    TextView signIn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView togglePasswordVisibility;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        togglePasswordVisibility  = findViewById(R.id.password_visibility_toggle);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        signUp = findViewById(R.id.signup_button);
        name = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        phone = findViewById(R.id.signup_phone);
        address = findViewById(R.id.signup_address);
        signIn = findViewById(R.id.loginRedirectText);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });

        togglePasswordVisibility .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionStart = password.getSelectionStart();
                int selectionEnd = password.getSelectionEnd();

                if (password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.baseline_remove_red_eye_24);
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.baseline_visibility_off_24);
                }

                password.setSelection(selectionStart, selectionEnd);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                createUser();
            }
        });
    }

    private void createUser() {
        String userName = name.getText().toString();
        String userPass = password.getText().toString();
        String userEmail = email.getText().toString();
        String userPhone = phone.getText().toString();
        String userAddress = address.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Имя пустое", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPass)) {
            Toast.makeText(this, "Пароль пустой", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Почта пустая", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPhone)) {
            Toast.makeText(this, "Телефон пустой", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userAddress)) {
            Toast.makeText(this, "Адрес пустой", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPhone.length() < 11) {
            Toast.makeText(this, "Телефон не должен быть меньше 11 цифр", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPass.length() < 6) {
            Toast.makeText(this, "Пароль не должен быть меньше 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            UserModel userModel = new UserModel(userName, userEmail, userPass, userPhone, userAddress);
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(userModel);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistrationActivity.this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistrationActivity.this, "Ошибка регистрации: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}