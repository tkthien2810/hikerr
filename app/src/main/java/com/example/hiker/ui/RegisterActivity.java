package com.example.hiker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hiker.HomeActivity;
import com.example.hiker.R;
import com.example.hiker.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        userRepository = new UserRepository(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (password.equals(confirmPassword)) {
                    registerUser(username, password);
                } else {
                    // Hiển thị thông báo lỗi nếu mật khẩu không khớp
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String username, String password) {
        userRepository.insertUser(username, password, new UserRepository.RegisterCallback() {
            @Override
            public void onRegisterResult(boolean success) {
                if (success) {
                    // Đăng ký thành công, chuyển hướng đến HomeActivity hoặc LoginActivity để đăng nhập
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng RegisterActivity để người dùng không quay lại

                    // Hoặc chuyển hướng đến LoginActivity để đăng nhập
                    // Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    // startActivity(loginIntent);
                    // finish();
                } else {
                    // Hiển thị thông báo lỗi đăng ký
                    Toast.makeText(RegisterActivity.this, "Đăng ký không thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
