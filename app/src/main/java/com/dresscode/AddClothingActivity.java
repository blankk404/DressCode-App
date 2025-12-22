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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.dresscode.data.entity.ClothingItem;
import com.dresscode.data.entity.Category;
import com.dresscode.databinding.ActivityAddClothingBinding;
import com.dresscode.viewmodel.ClothingViewModel;

/**
 * 添加衣物页面
 */
public class AddClothingActivity extends AppCompatActivity {

    private ActivityAddClothingBinding binding;
    private ClothingViewModel clothingViewModel;
    private SharedPreferences sharedPreferences;
    private int userId;
    private Uri selectedImageUri;
    private int selectedCategoryId = -1;
    private java.util.List<Category> categoryList = new java.util.ArrayList<>();
    private int editClothingId = -1;
    private boolean isEditMode = false;
    private ClothingItem existingItem;

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
                    }
                    binding.ivClothingImage.setImageURI(selectedImageUri);
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
        binding = ActivityAddClothingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViewModel();
        initMode();
        setupSpinners();
        setupClickListeners();
        observeViewModel();
    }

    private void initViewModel() {
        clothingViewModel = new ViewModelProvider(this).get(ClothingViewModel.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
    }

    private void initMode() {
        editClothingId = getIntent().getIntExtra("clothing_id", -1);
        isEditMode = editClothingId != -1;
        if (isEditMode) {
            binding.tvTitle.setText(getString(R.string.edit_clothing_title));
            loadExistingItem();
        } else {
            binding.tvTitle.setText(getString(R.string.add_clothing_title));
        }
    }

    private void loadExistingItem() {
        clothingViewModel.getClothingItemById(editClothingId).observe(this, item -> {
            if (item == null) return;
            existingItem = item;
            // 回填（不依赖 categoryList 时就先回填基本字段）
            binding.etName.setText(item.getName());
            binding.etBrand.setText(item.getBrand());
            binding.etColor.setText(item.getColor());
            binding.etSize.setText(item.getSize());
            if (!TextUtils.isEmpty(item.getSeason())) {
                binding.actvSeason.setText(item.getSeason(), false);
            }

            if (!TextUtils.isEmpty(item.getImagePath())) {
                try {
                    selectedImageUri = Uri.parse(item.getImagePath());
                    binding.ivClothingImage.setImageURI(selectedImageUri);
                } catch (Exception ignored) {
                }
            }

            // 类别回填：等 categories 加载后再设置（setupSpinners 里会处理）
            applyCategorySelectionIfReady();
        });
    }

    private void setupSpinners() {
        // 类别下拉框：从数据库读取真实 categoryId，避免外键错误
        binding.btnSave.setEnabled(false);
        clothingViewModel.getAllCategories().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                // 种子数据可能还在写入，稍后会再回调
                return;
            }
            categoryList = categories;
            java.util.List<String> names = new java.util.ArrayList<>();
            for (Category c : categories) {
                names.add(c.getName());
            }
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, names);
            binding.actvCategory.setAdapter(categoryAdapter);

            // 默认选择/回填
            int selectedIndex = 0;
            if (isEditMode && existingItem != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getId() == existingItem.getCategoryId()) {
                        selectedIndex = i;
                        break;
                    }
                }
            }
            binding.actvCategory.setText(names.get(selectedIndex), false);
            selectedCategoryId = categories.get(selectedIndex).getId();
            binding.btnSave.setEnabled(true);
        });

        binding.actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            if (categoryList != null && position >= 0 && position < categoryList.size()) {
                selectedCategoryId = categoryList.get(position).getId();
            }
        });

        // 季节下拉框
        String[] seasons = {"春", "夏", "秋", "冬", "四季"};
        ArrayAdapter<String> seasonAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, seasons);
        binding.actvSeason.setAdapter(seasonAdapter);
        if (isEditMode && existingItem != null && !TextUtils.isEmpty(existingItem.getSeason())) {
            binding.actvSeason.setText(existingItem.getSeason(), false);
        } else {
            binding.actvSeason.setText(seasons[4], false);
        }
    }

    private void applyCategorySelectionIfReady() {
        if (!isEditMode || existingItem == null) return;
        if (categoryList == null || categoryList.isEmpty()) return;
        for (int i = 0; i < categoryList.size(); i++) {
            Category c = categoryList.get(i);
            if (c.getId() == existingItem.getCategoryId()) {
                binding.actvCategory.setText(c.getName(), false);
                selectedCategoryId = c.getId();
                return;
            }
        }
    }

    private void setupClickListeners() {
        // 返回
        binding.btnBack.setOnClickListener(v -> finish());

        // 选择图片
        binding.btnSelectImage.setOnClickListener(v -> checkPermissionAndPickImage());

        // 保存
        binding.btnSave.setOnClickListener(v -> saveClothingItem());
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

    private void saveClothingItem() {
        if (userId == -1) {
            Toast.makeText(this, "登录状态失效，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCategoryId == -1) {
            Toast.makeText(this, "类别数据未初始化完成，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取输入
        String name = binding.etName.getText().toString().trim();
        String brand = binding.etBrand.getText().toString().trim();
        String color = binding.etColor.getText().toString().trim();
        String size = binding.etSize.getText().toString().trim();
        String season = binding.actvSeason.getText().toString().trim();

        // 验证
        if (TextUtils.isEmpty(name)) {
            binding.tilName.setError("请输入衣物名称");
            return;
        } else {
            binding.tilName.setError(null);
        }

        // 创建/复用衣物对象（编辑时保留未展示字段）
        ClothingItem item = (isEditMode && existingItem != null) ? existingItem : new ClothingItem();
        if (isEditMode) {
            item.setId(editClothingId);
        }
        item.setUserId(userId);
        item.setCategoryId(selectedCategoryId);
        item.setName(name);
        item.setBrand(brand);
        item.setColor(color);
        item.setSize(size);
        item.setSeason(season);
        
        if (selectedImageUri != null) {
            item.setImagePath(selectedImageUri.toString());
        }

        // 保存
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);

        if (isEditMode) {
            clothingViewModel.updateClothingItem(item, null);
        } else {
            clothingViewModel.insertClothingItem(item, null);
        }
    }

    private void observeViewModel() {
        clothingViewModel.getOperationMessage().observe(this, message -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if ("添加成功".equals(message) || "更新成功".equals(message)) {
                finish();
            }
        });

        clothingViewModel.getIsLoading().observe(this, isLoading -> {
            if (!isLoading) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSave.setEnabled(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

