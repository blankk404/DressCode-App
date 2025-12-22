package com.dresscode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dresscode.data.entity.Category;
import com.dresscode.databinding.ActivityClothingDetailBinding;
import com.dresscode.viewmodel.ClothingViewModel;

/**
 * 衣物详情页面
 */
public class ClothingDetailActivity extends AppCompatActivity {

    private ActivityClothingDetailBinding binding;
    private ClothingViewModel clothingViewModel;
    private int clothingId;
    private java.util.List<Category> categoryList = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClothingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        clothingId = getIntent().getIntExtra("clothing_id", -1);
        
        initViewModel();
        loadClothingDetail();
        setupClickListeners();
    }

    private void initViewModel() {
        clothingViewModel = new ViewModelProvider(this).get(ClothingViewModel.class);
        clothingViewModel.getAllCategories().observe(this, list -> {
            categoryList = (list == null) ? new java.util.ArrayList<>() : list;
        });
    }

    private void loadClothingDetail() {
        if (clothingId != -1) {
            clothingViewModel.getClothingItemById(clothingId).observe(this, item -> {
                if (item != null) {
                    // 显示信息
                    binding.tvClothingName.setText(item.getName());
                    binding.tvSeason.setText(item.getSeason());
                    binding.tvBrand.setText(TextUtils.isEmpty(item.getBrand()) ? "-" : item.getBrand());
                    binding.tvColor.setText(TextUtils.isEmpty(item.getColor()) ? "-" : item.getColor());
                    binding.tvSize.setText(TextUtils.isEmpty(item.getSize()) ? "-" : item.getSize());

                    // 显示类别
                    String catName = "-";
                    for (Category c : categoryList) {
                        if (c.getId() == item.getCategoryId()) {
                            catName = c.getName();
                            break;
                        }
                    }
                    binding.tvCategory.setText(catName);

                    // 加载图片
                    if (!TextUtils.isEmpty(item.getImagePath())) {
                        try {
                            Uri imageUri = Uri.parse(item.getImagePath());
                            binding.ivClothingImage.setImageURI(imageUri);
                        } catch (Exception e) {
                            binding.ivClothingImage.setImageResource(R.drawable.logo_background);
                        }
                    }
                }
            });
        }
    }

    private void setupClickListeners() {
        // 返回
        binding.btnBack.setOnClickListener(v -> finish());

        // 编辑
        binding.btnEdit.setOnClickListener(v -> {
            if (clothingId == -1) return;
            Intent intent = new Intent(this, AddClothingActivity.class);
            intent.putExtra("clothing_id", clothingId);
            startActivity(intent);
        });

        // 删除
        binding.btnDelete.setOnClickListener(v -> showDeleteDialog());
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
            .setTitle("删除衣物")
            .setMessage("确定要删除这件衣物吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                if (clothingId != -1) {
                    clothingViewModel.getClothingItemById(clothingId).observe(this, item -> {
                        if (item != null) {
                            clothingViewModel.deleteClothingItem(item);
                            clothingViewModel.getOperationMessage().observe(this, message -> {
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }
                    });
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}