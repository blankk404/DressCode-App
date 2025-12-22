package com.dresscode;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dresscode.data.entity.BodyProfile;
import com.dresscode.databinding.ActivityBodyProfileBinding;
import com.dresscode.viewmodel.BodyProfileViewModel;

import java.util.Locale;

/**
 * 身材档案管理页面
 */
public class BodyProfileActivity extends AppCompatActivity {

    private ActivityBodyProfileBinding binding;
    private BodyProfileViewModel bodyProfileViewModel;
    private SharedPreferences sharedPreferences;
    private int userId;
    private BodyProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBodyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 隐藏ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViewModel();
        loadBodyProfile();
        setupClickListeners();
        setupTextWatchers();
    }

    private void initViewModel() {
        bodyProfileViewModel = new ViewModelProvider(this).get(BodyProfileViewModel.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
    }

    private void loadBodyProfile() {
        if (userId != -1) {
            bodyProfileViewModel.getBodyProfileByUserId(userId).observe(this, bodyProfile -> {
                if (bodyProfile != null) {
                    currentProfile = bodyProfile;
                    fillDataToUI(bodyProfile);
                }
            });
        }
    }

    private void fillDataToUI(BodyProfile profile) {
        // 填充数据
        if (profile.getHeight() > 0) {
            binding.etHeight.setText(String.valueOf(profile.getHeight()));
        }
        if (profile.getWeight() > 0) {
            binding.etWeight.setText(String.valueOf(profile.getWeight()));
        }
        if (profile.getHeadCircumference() > 0) {
            binding.etHead.setText(String.valueOf(profile.getHeadCircumference()));
        }
        if (profile.getShoulderWidth() > 0) {
            binding.etShoulder.setText(String.valueOf(profile.getShoulderWidth()));
        }
        if (profile.getChestCircumference() > 0) {
            binding.etChest.setText(String.valueOf(profile.getChestCircumference()));
        }
        if (profile.getWaistCircumference() > 0) {
            binding.etWaist.setText(String.valueOf(profile.getWaistCircumference()));
        }
        if (profile.getHipCircumference() > 0) {
            binding.etHip.setText(String.valueOf(profile.getHipCircumference()));
        }
        if (profile.getShoeSizeEu() > 0) {
            binding.etShoe.setText(String.valueOf(profile.getShoeSizeEu()));
        }

        // 更新BMI显示
        updateBMIDisplay(profile.getBmi(), profile.getBMIStatus());
    }

    private void setupClickListeners() {
        // 返回
        binding.btnBack.setOnClickListener(v -> finish());

        // 保存
        binding.btnSave.setOnClickListener(v -> saveBodyProfile());
    }

    private void setupTextWatchers() {
        // 监听身高和体重变化，自动计算BMI
        TextWatcher bmiWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateAndDisplayBMI();
            }
        };

        binding.etHeight.addTextChangedListener(bmiWatcher);
        binding.etWeight.addTextChangedListener(bmiWatcher);
    }

    private void calculateAndDisplayBMI() {
        String heightStr = binding.etHeight.getText().toString().trim();
        String weightStr = binding.etWeight.getText().toString().trim();

        if (!TextUtils.isEmpty(heightStr) && !TextUtils.isEmpty(weightStr)) {
            try {
                float height = Float.parseFloat(heightStr);
                float weight = Float.parseFloat(weightStr);
                
                if (height > 0 && weight > 0) {
                    float bmi = BodyProfile.calculateBMI(height, weight);
                    String status = getBMIStatus(bmi);
                    updateBMIDisplay(bmi, status);
                }
            } catch (NumberFormatException e) {
                // 忽略格式错误
            }
        }
    }

    private String getBMIStatus(float bmi) {
        if (bmi < 18.5) {
            return "偏瘦";
        } else if (bmi < 24) {
            return "正常";
        } else if (bmi < 28) {
            return "偏胖";
        } else {
            return "肥胖";
        }
    }

    private void updateBMIDisplay(float bmi, String status) {
        binding.tvBMIValue.setText(String.format(Locale.getDefault(), "%.1f", bmi));
        binding.tvBMIStatus.setText(status);
    }

    private void saveBodyProfile() {
        // 获取输入
        String heightStr = binding.etHeight.getText().toString().trim();
        String weightStr = binding.etWeight.getText().toString().trim();
        String headStr = binding.etHead.getText().toString().trim();
        String shoulderStr = binding.etShoulder.getText().toString().trim();
        String chestStr = binding.etChest.getText().toString().trim();
        String waistStr = binding.etWaist.getText().toString().trim();
        String hipStr = binding.etHip.getText().toString().trim();
        String shoeStr = binding.etShoe.getText().toString().trim();

        // 验证必填项
        boolean isValid = true;

        if (TextUtils.isEmpty(heightStr)) {
            binding.tilHeight.setError("请输入身高");
            isValid = false;
        } else {
            binding.tilHeight.setError(null);
        }

        if (TextUtils.isEmpty(weightStr)) {
            binding.tilWeight.setError("请输入体重");
            isValid = false;
        } else {
            binding.tilWeight.setError(null);
        }

        if (!isValid) {
            return;
        }

        try {
            // 创建或更新档案
            BodyProfile profile;
            if (currentProfile != null) {
                profile = currentProfile;
            } else {
                profile = new BodyProfile();
                profile.setUserId(userId);
            }

            // 设置数据
            profile.setHeight(Float.parseFloat(heightStr));
            profile.setWeight(Float.parseFloat(weightStr));
            
            if (!TextUtils.isEmpty(headStr)) {
                profile.setHeadCircumference(Float.parseFloat(headStr));
            }
            if (!TextUtils.isEmpty(shoulderStr)) {
                profile.setShoulderWidth(Float.parseFloat(shoulderStr));
            }
            if (!TextUtils.isEmpty(chestStr)) {
                profile.setChestCircumference(Float.parseFloat(chestStr));
            }
            if (!TextUtils.isEmpty(waistStr)) {
                profile.setWaistCircumference(Float.parseFloat(waistStr));
            }
            if (!TextUtils.isEmpty(hipStr)) {
                profile.setHipCircumference(Float.parseFloat(hipStr));
            }
            if (!TextUtils.isEmpty(shoeStr)) {
                profile.setShoeSizeEu(Float.parseFloat(shoeStr));
            }

            // 保存
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnSave.setEnabled(false);

            bodyProfileViewModel.saveBodyProfile(profile);

            // 观察保存结果
            bodyProfileViewModel.getSaveMessage().observe(this, message -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });

            bodyProfileViewModel.getIsLoading().observe(this, isLoading -> {
                if (!isLoading) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSave.setEnabled(true);
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

