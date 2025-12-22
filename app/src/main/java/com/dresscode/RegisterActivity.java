package com.dresscode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dresscode.databinding.ActivityRegisterBinding;
import com.dresscode.viewmodel.UserViewModel;

/**
 * 注册页面
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
    }

    private void setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 注册按钮
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        // 去登录
        binding.tvGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void attemptRegister() {
        // 获取输入
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String passwordConfirm = binding.etPasswordConfirm.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String nickname = binding.etNickname.getText().toString().trim();

        // 获取性别
        String gender = "男";
        int selectedId = binding.rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rbFemale) {
            gender = "女";
        } else if (selectedId == R.id.rbSecret) {
            gender = "保密";
        }

        // 验证输入
        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError("请输入用户名");
            isValid = false;
        } else if (username.length() < 3) {
            binding.tilUsername.setError("用户名至少3位");
            isValid = false;
        } else {
            binding.tilUsername.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("请输入密码");
            isValid = false;
        } else if (password.length() < 6) {
            binding.tilPassword.setError("密码至少6位");
            isValid = false;
        } else {
            binding.tilPassword.setError(null);
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            binding.tilPasswordConfirm.setError("请确认密码");
            isValid = false;
        } else if (!password.equals(passwordConfirm)) {
            binding.tilPasswordConfirm.setError("两次密码不一致");
            isValid = false;
        } else {
            binding.tilPasswordConfirm.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            binding.tilPhone.setError("请输入手机号");
            isValid = false;
        } else if (!phone.matches("^1[3-9]\\d{9}$")) {
            binding.tilPhone.setError("手机号格式不正确");
            isValid = false;
        } else {
            binding.tilPhone.setError(null);
        }

        if (TextUtils.isEmpty(nickname)) {
            binding.tilNickname.setError("请输入昵称");
            isValid = false;
        } else {
            binding.tilNickname.setError(null);
        }

        if (!isValid) {
            return;
        }

        // 执行注册
        userViewModel.registerUser(username, password, phone, nickname, gender);
    }

    private void observeViewModel() {
        // 观察加载状态
        userViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.btnRegister.setEnabled(false);
                binding.btnRegister.setText("");
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.btnRegister.setEnabled(true);
                binding.btnRegister.setText(R.string.register_button);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        // 观察注册结果
        userViewModel.getRegisterMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        // 观察当前用户
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // 保存登录状态
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", user.getId());
                editor.putString("username", user.getUsername());
                editor.putBoolean("is_logged_in", true);
                editor.apply();

                // 跳转到主页
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

