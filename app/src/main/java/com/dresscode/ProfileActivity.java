package com.dresscode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dresscode.databinding.ActivityProfileBinding;
import com.dresscode.viewmodel.UserViewModel;

import java.util.concurrent.TimeUnit;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViewModel();
        loadUserInfo();
        setupClickListeners();
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
    }

    private void loadUserInfo() {
        if (userId != -1) {
            userViewModel.getUserById(userId).observe(this, user -> {
                if (user != null) {
                    // 更新UI显示用户信息
                    binding.tvNickname.setText(user.getNickname());
                    
                    // 计算加入天数
                    long days = TimeUnit.MILLISECONDS.toDays(
                        System.currentTimeMillis() - user.getCreatedAt()
                    );
                    String userInfo = user.getGender() + " · 已加入" + days + "天";
                    binding.tvUserInfo.setText(userInfo);
                    
                    // 加载头像
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        try {
                            Uri avatarUri = Uri.parse(user.getAvatar());
                            binding.ivAvatar.setImageURI(avatarUri);
                        } catch (Exception e) {
                            // 使用默认头像
                        }
                    }
                }
            });
        }
    }

    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> finish());

        binding.ivSettings.setOnClickListener(v -> {
            Toast.makeText(this, "功能开发中...", Toast.LENGTH_SHORT).show();
        });

        // 编辑资料
        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // 我的身材档案
        binding.llBodyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, BodyProfileActivity.class);
            startActivity(intent);
        });

        binding.llMyOutfits.setOnClickListener(v -> {
            Toast.makeText(this, "功能开发中...", Toast.LENGTH_SHORT).show();
        });

        binding.llMyCollections.setOnClickListener(v -> {
            Toast.makeText(this, "功能开发中...", Toast.LENGTH_SHORT).show();
        });

        binding.llMyLikes.setOnClickListener(v -> {
            Toast.makeText(this, "功能开发中...", Toast.LENGTH_SHORT).show();
        });

        binding.llHelp.setOnClickListener(v -> {
            Toast.makeText(this, "功能开发中...", Toast.LENGTH_SHORT).show();
        });

        binding.llAbout.setOnClickListener(v -> {
            Toast.makeText(this, "功能开发中...", Toast.LENGTH_SHORT).show();
        });

        // 退出登录
        binding.btnLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？")
            .setPositiveButton("确定", (dialog, which) -> {
                // 清除登录状态
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                
                // 清除ViewModel中的用户信息
                userViewModel.logout();
                
                // 跳转到启动页
                Intent intent = new Intent(ProfileActivity.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到此页面时重新加载用户信息
        loadUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}