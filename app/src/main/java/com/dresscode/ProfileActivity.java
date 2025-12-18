package com.dresscode;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.dresscode.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.ivBack.setOnClickListener(v -> finish());

        binding.ivSettings.setOnClickListener(v -> {
            // TODO: 打开设置页面
        });

        binding.btnEdit.setOnClickListener(v -> {
            // TODO: 打开编辑资料页面
        });

        binding.llMyOutfits.setOnClickListener(v -> {
            // TODO: 打开我的穿搭页面
        });

        binding.llMyCollections.setOnClickListener(v -> {
            // TODO: 打开我的收藏页面
        });

        binding.llMyLikes.setOnClickListener(v -> {
            // TODO: 打开我的点赞页面
        });

        binding.llHelp.setOnClickListener(v -> {
            // TODO: 打开帮助中心页面
        });

        binding.llAbout.setOnClickListener(v -> {
            // TODO: 打开关于我们页面
        });

        binding.btnLogout.setOnClickListener(v -> {
            // TODO: 实现退出登录功能
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}