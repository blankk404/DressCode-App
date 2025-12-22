package com.dresscode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.dresscode.data.entity.Outfit;
import com.dresscode.data.entity.OutfitItemCrossRef;
import com.dresscode.data.entity.OutfitItemWithClothing;
import com.dresscode.databinding.ActivityOutfitDetailBinding;
import com.dresscode.utils.BitmapStore;
import com.dresscode.utils.ImageLoader;
import com.dresscode.utils.MultiTouchTransformListener;
import com.dresscode.viewmodel.OutfitViewModel;

import java.util.ArrayList;
import java.util.List;

public class OutfitDetailActivity extends AppCompatActivity {

    private ActivityOutfitDetailBinding binding;
    private OutfitViewModel outfitViewModel;
    private int outfitId;
    private Outfit currentOutfit;
    private final List<OutfitItemWithClothing> currentItems = new ArrayList<>();
    private final List<View> canvasViews = new ArrayList<>();
    private final MultiTouchTransformListener transformListener = new MultiTouchTransformListener();
    private boolean deleting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutfitDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        outfitId = getIntent().getIntExtra("outfit_id", -1);
        outfitViewModel = new ViewModelProvider(this).get(OutfitViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveEdits());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());

        outfitViewModel.getMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        loadOutfit();
    }

    private void loadOutfit() {
        if (outfitId == -1) return;

        outfitViewModel.getOutfitById(outfitId).observe(this, outfit -> {
            currentOutfit = outfit;
            if (deleting && outfit == null) {
                finish();
            }
        });

        outfitViewModel.getOutfitItemsWithClothing(outfitId).observe(this, list -> {
            currentItems.clear();
            if (list != null) currentItems.addAll(list);
            renderCanvasFromRefs();
        });
    }

    private void renderCanvasFromRefs() {
        binding.canvasContainer.removeAllViews();
        canvasViews.clear();

        int canvasW = binding.canvasContainer.getWidth();
        int canvasH = binding.canvasContainer.getHeight();

        // 如果还没布局完成，post 一次再渲染
        if (canvasW == 0 || canvasH == 0) {
            binding.canvasContainer.post(this::renderCanvasFromRefs);
            return;
        }

        for (OutfitItemWithClothing item : currentItems) {
            if (item == null || item.clothingItem == null || item.ref == null) continue;
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new android.widget.FrameLayout.LayoutParams(280, 360));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.load(iv, item.clothingItem.getImagePath(), R.drawable.logo_background);

            OutfitItemCrossRef ref = item.ref;
            iv.setX(ref.getRelX() * canvasW);
            iv.setY(ref.getRelY() * canvasH);
            iv.setScaleX(ref.getScale());
            iv.setScaleY(ref.getScale());
            iv.setRotation(ref.getRotation());

            iv.setOnTouchListener(transformListener);
            binding.canvasContainer.addView(iv);
            canvasViews.add(iv);
        }
    }

    private void saveEdits() {
        if (outfitId == -1) return;
        try {
            Bitmap bitmap = Bitmap.createBitmap(binding.canvasContainer.getWidth(), binding.canvasContainer.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            binding.canvasContainer.draw(canvas);
            String collagePath = BitmapStore.saveOutfitCollage(this, bitmap);

            int canvasW = binding.canvasContainer.getWidth();
            int canvasH = binding.canvasContainer.getHeight();
            List<OutfitItemCrossRef> refs = new ArrayList<>();
            for (int i = 0; i < currentItems.size() && i < canvasViews.size(); i++) {
                OutfitItemWithClothing item = currentItems.get(i);
                View v = canvasViews.get(i);
                float relX = canvasW == 0 ? 0 : (v.getX() / canvasW);
                float relY = canvasH == 0 ? 0 : (v.getY() / canvasH);
                float scale = v.getScaleX();
                float rotation = v.getRotation();
                refs.add(new OutfitItemCrossRef(outfitId, item.clothingItem.getId(), relX, relY, scale, rotation));
            }

            String scene = currentOutfit != null ? currentOutfit.getScene() : null;
            String season = currentOutfit != null ? currentOutfit.getSeason() : null;
            outfitViewModel.updateOutfit(outfitId, collagePath, scene, season, refs);
        } catch (Exception e) {
            Toast.makeText(this, "保存失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        if (outfitId == -1) return;
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("删除搭配")
                .setMessage("确定删除该搭配吗？删除后卡片（包含上身照）将一并移除。")
                .setPositiveButton("删除", (d, w) -> deleteOutfit())
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteOutfit() {
        deleting = true;
        // 尝试删除本地拼图文件（上身照是 content uri，不强删用户相册文件）
        try {
            if (currentOutfit != null && currentOutfit.getCollagePath() != null) {
                java.io.File f = new java.io.File(currentOutfit.getCollagePath());
                if (f.exists()) {
                    // ignore result
                    //noinspection ResultOfMethodCallIgnored
                    f.delete();
                }
            }
        } catch (Exception ignored) {
        }
        outfitViewModel.deleteOutfit(outfitId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}


