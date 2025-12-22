package com.dresscode;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dresscode.ai.SiliconFlowClient;
import com.dresscode.adapter.AiImageAdapter;
import com.dresscode.databinding.ActivityAiRecommendBinding;

import java.util.List;
import java.util.concurrent.Executors;

public class AiRecommendActivity extends AppCompatActivity {

    private ActivityAiRecommendBinding binding;
    private AiImageAdapter adapter;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiRecommendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        query = getIntent().getStringExtra("query");
        binding.tvTitle.setText("AI推荐 · " + (query == null ? "" : query));

        adapter = new AiImageAdapter();
        binding.rv.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rv.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnRefresh.setOnClickListener(v -> fetch());

        fetch();
    }

    private void fetch() {
        binding.progress.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<String> urls = new SiliconFlowClient().fetchXhsImageUrls(query == null ? "" : query);
                runOnUiThread(() -> {
                    binding.progress.setVisibility(View.GONE);
                    adapter.setUrls(urls);
                    if (urls == null || urls.isEmpty()) {
                        Toast.makeText(this, "暂无结果，请换关键词或稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    binding.progress.setVisibility(View.GONE);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}


