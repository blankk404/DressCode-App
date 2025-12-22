package com.dresscode;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dresscode.data.entity.User;
import com.dresscode.databinding.ActivityEditProfileBinding;
import com.dresscode.viewmodel.UserViewModel;

/**
 * 编辑资料页面
 */
public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private UserViewModel userViewModel;
    private SharedPreferences sharedPreferences;
    private int userId;
    private User currentUser;
    private Uri selectedImageUri;

    // 图片选择器
    private final ActivityResultLauncher<Intent> imagePickerLauncher = 
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                if (selectedImageUri != null) {
                    // 持久化URI读权限，避免切页/重启后图片丢失
                    try {
                        final int takeFlags = (result.getData().getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                        getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                    } catch (Exception ignored) {
                        // 某些URI可能不支持持久化权限（例如非SAF），这里忽略即可
                    }
                    binding.ivAvatar.setImageURI(selectedImageUri);
                }
            }
        });

    // 权限请求
    private final ActivityResultLauncher<String> permissionLauncher = 
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openImagePicker();
            } else {
                Toast.makeText(this, "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show();
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViewModel();
        loadUserData();
        setupClickListeners();
    }

    private void initViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
    }

    private void loadUserData() {
        if (userId != -1) {
            userViewModel.getUserById(userId).observe(this, user -> {
                if (user != null) {
                    currentUser = user;
                    // 填充数据到UI
                    binding.etNickname.setText(user.getNickname());
                    binding.etUsername.setText(user.getUsername());
                    binding.etPhone.setText(user.getPhone());
                    
                    // 设置性别
                    if ("男".equals(user.getGender())) {
                        binding.rgGender.check(R.id.rbMale);
                    } else if ("女".equals(user.getGender())) {
                        binding.rgGender.check(R.id.rbFemale);
                    } else {
                        binding.rgGender.check(R.id.rbSecret);
                    }

                    // 加载头像
                    if (!TextUtils.isEmpty(user.getAvatar())) {
                        try {
                            binding.ivAvatar.setImageURI(Uri.parse(user.getAvatar()));
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
        }
    }

    private void setupClickListeners() {
        // 返回
        binding.btnBack.setOnClickListener(v -> finish());

        // 保存
        binding.btnSave.setOnClickListener(v -> saveProfile());

        // 更换头像
        binding.btnChangeAvatar.setOnClickListener(v -> checkPermissionAndPickImage());
    }

    private void checkPermissionAndPickImage() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        // 使用SAF，支持持久化URI权限
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        imagePickerLauncher.launch(intent);
    }

    private void saveProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "用户信息加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取输入
        String nickname = binding.etNickname.getText().toString().trim();

        // 验证
        if (TextUtils.isEmpty(nickname)) {
            binding.tilNickname.setError("请输入昵称");
            return;
        } else {
            binding.tilNickname.setError(null);
        }

        // 获取性别
        String gender = "男";
        int selectedId = binding.rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rbFemale) {
            gender = "女";
        } else if (selectedId == R.id.rbSecret) {
            gender = "保密";
        }

        // 更新用户信息
        currentUser.setNickname(nickname);
        currentUser.setGender(gender);
        
        // 如果选择了新头像，保存URI
        if (selectedImageUri != null) {
            currentUser.setAvatar(selectedImageUri.toString());
        }

        // 显示加载
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);

        // 保存到数据库
        userViewModel.updateUser(currentUser);

        // 观察更新结果
        userViewModel.getIsLoading().observe(this, isLoading -> {
            if (!isLoading) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSave.setEnabled(true);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
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

