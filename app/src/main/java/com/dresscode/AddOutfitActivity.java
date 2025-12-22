package com.dresscode;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dresscode.adapter.ClothingPickAdapter;
import com.dresscode.data.entity.ClothingItem;
import com.dresscode.data.entity.Outfit;
import com.dresscode.data.entity.OutfitItemCrossRef;
import com.dresscode.databinding.ActivityAddOutfitBinding;
import com.dresscode.databinding.BottomsheetPickClothingBinding;
import com.dresscode.utils.BitmapStore;
import com.dresscode.utils.ImageLoader;
import com.dresscode.utils.MultiTouchTransformListener;
import com.dresscode.viewmodel.ClothingViewModel;
import com.dresscode.viewmodel.OutfitViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class AddOutfitActivity extends AppCompatActivity {

    private ActivityAddOutfitBinding binding;
    private ClothingViewModel clothingViewModel;
    private OutfitViewModel outfitViewModel;
    private int userId;

    private final List<ClothingItem> selectedItems = new ArrayList<>();
    private final List<View> canvasItemViews = new ArrayList<>();
    private final MultiTouchTransformListener transformListener = new MultiTouchTransformListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOutfitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        clothingViewModel = new ViewModelProvider(this).get(ClothingViewModel.class);
        outfitViewModel = new ViewModelProvider(this).get(OutfitViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnPickItems.setOnClickListener(v -> openPickBottomSheet());
        binding.btnClear.setOnClickListener(v -> clearCanvas());
        binding.btnSave.setOnClickListener(v -> showMetaAndSave());

        outfitViewModel.getMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
        outfitViewModel.getSavedOutfitId().observe(this, id -> {
            if (id != null && id > 0) finish();
        });
    }

    private void clearCanvas() {
        selectedItems.clear();
        binding.canvasContainer.removeAllViews();
        canvasItemViews.clear();
    }

    private void openPickBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        BottomsheetPickClothingBinding bs = BottomsheetPickClothingBinding.inflate(getLayoutInflater());
        dialog.setContentView(bs.getRoot());

        ClothingPickAdapter pickAdapter = new ClothingPickAdapter();
        bs.rv.setLayoutManager(new GridLayoutManager(this, 3));
        bs.rv.setAdapter(pickAdapter);

        // 观察全量衣物并本地筛选（避免频繁创建LiveData）
        clothingViewModel.getClothingItemsByUserId(userId).observe(this, items -> {
            pickAdapter.setItems(items);
        });

        final int[] filter = {0}; // 0=全部
        View.OnClickListener filterClick = v -> {
            if (v == bs.btnAll) filter[0] = 0;
            else if (v == bs.btnTop) filter[0] = 1;
            else if (v == bs.btnCoat) filter[0] = 2;
            else if (v == bs.btnPants) filter[0] = 3;
            else if (v == bs.btnSkirt) filter[0] = 4;
            else if (v == bs.btnShoes) filter[0] = 5;
            else if (v == bs.btnAccessory) filter[0] = 6;

            clothingViewModel.getClothingItemsByUserId(userId).observe(this, items -> {
                if (items == null) {
                    pickAdapter.setItems(null);
                    return;
                }
                if (filter[0] == 0) {
                    pickAdapter.setItems(items);
                } else {
                    List<ClothingItem> filtered = new ArrayList<>();
                    for (ClothingItem it : items) {
                        if (it.getCategoryId() == filter[0]) filtered.add(it);
                    }
                    pickAdapter.setItems(filtered);
                }
            });
        };

        bs.btnAll.setOnClickListener(filterClick);
        bs.btnTop.setOnClickListener(filterClick);
        bs.btnCoat.setOnClickListener(filterClick);
        bs.btnPants.setOnClickListener(filterClick);
        bs.btnSkirt.setOnClickListener(filterClick);
        bs.btnShoes.setOnClickListener(filterClick);
        bs.btnAccessory.setOnClickListener(filterClick);

        bs.btnDone.setOnClickListener(v -> {
            selectedItems.clear();
            selectedItems.addAll(pickAdapter.getSelectedItems());
            renderItemsOnCanvas();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void renderItemsOnCanvas() {
        binding.canvasContainer.removeAllViews();
        canvasItemViews.clear();

        // 简单摆放：按序堆叠，用户可拖拽调整
        for (int i = 0; i < selectedItems.size(); i++) {
            ClothingItem item = selectedItems.get(i);
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new android.widget.FrameLayout.LayoutParams(280, 360));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setBackgroundColor(getResources().getColor(R.color.bg_gray));
            ImageLoader.load(iv, item.getImagePath(), R.drawable.logo_background);

            iv.setX(20 + (i % 2) * 80);
            iv.setY(20 + (i / 2) * 120);
            iv.setOnTouchListener(transformListener);

            binding.canvasContainer.addView(iv);
            canvasItemViews.add(iv);
        }
    }

    // DragTouchListener 已由 MultiTouchTransformListener 替代（支持缩放/旋转）

    private void showMetaAndSave() {
        if (selectedItems.isEmpty() || binding.canvasContainer.getChildCount() == 0) {
            Toast.makeText(this, "请先从衣橱选择单品进行拼图", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_outfit_meta, null);
        android.widget.AutoCompleteTextView actScene = dialogView.findViewById(R.id.actScene);
        android.widget.AutoCompleteTextView actSeason = dialogView.findViewById(R.id.actSeason);

        String[] scenes = {"工作", "休闲", "运动", "约会", "旅行"};
        actScene.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, scenes));
        actScene.setText(scenes[0], false);

        String[] seasons = {"春", "夏", "秋", "冬", "四季"};
        actSeason.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, seasons));
        actSeason.setText(seasons[4], false);

        new AlertDialog.Builder(this)
                .setTitle("选择场景与季节")
                .setView(dialogView)
                .setPositiveButton("保存", (d, w) -> saveOutfit(actScene.getText().toString(), actSeason.getText().toString()))
                .setNegativeButton("取消", null)
                .show();
    }

    private void saveOutfit(String scene, String season) {
        try {
            // 渲染画布为Bitmap（3:4区域）
            Bitmap bitmap = Bitmap.createBitmap(binding.canvasContainer.getWidth(), binding.canvasContainer.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            binding.canvasContainer.draw(canvas);

            String collagePath = BitmapStore.saveOutfitCollage(this, bitmap);

            Outfit outfit = new Outfit();
            outfit.setUserId(userId);
            outfit.setScene(scene);
            outfit.setSeason(season);
            outfit.setCollagePath(collagePath);

            // 保存每个单品的位置（相对坐标）
            List<OutfitItemCrossRef> refs = new ArrayList<>();
            int canvasW = binding.canvasContainer.getWidth();
            int canvasH = binding.canvasContainer.getHeight();
            for (int i = 0; i < selectedItems.size(); i++) {
                ClothingItem item = selectedItems.get(i);
                View v = canvasItemViews.get(i);
                float relX = canvasW == 0 ? 0 : (v.getX() / canvasW);
                float relY = canvasH == 0 ? 0 : (v.getY() / canvasH);
                float scale = v.getScaleX();
                float rotation = v.getRotation();
                refs.add(new OutfitItemCrossRef(0, item.getId(), relX, relY, scale, rotation));
            }

            outfitViewModel.saveOutfit(outfit, refs);
        } catch (Exception e) {
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}


