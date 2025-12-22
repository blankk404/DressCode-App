package com.dresscode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dresscode.databinding.ActivityLoginBinding;
import com.dresscode.viewmodel.UserViewModel;

/**
 * 登录页面
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
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

        // 登录按钮
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // 忘记密码
        binding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "功能开发中...", Toast.LENGTH_SHORT).show();
            }
        });

        // 去注册
        binding.tvGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void attemptLogin() {
        // 获取输入
        String account = binding.etAccount.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(account)) {
            binding.tilAccount.setError("请输入用户名或手机号");
            return;
        } else {
            binding.tilAccount.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError("请输入密码");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        if (password.length() < 6) {
            binding.tilPassword.setError("密码至少6位");
            return;
        }

        // 执行登录
        userViewModel.loginUser(account, password);
    }

    private void observeViewModel() {
        // 观察加载状态
        userViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setText("");
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText(R.string.login_button);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        // 观察登录结果
        userViewModel.getLoginMessage().observe(this, message -> {
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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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

