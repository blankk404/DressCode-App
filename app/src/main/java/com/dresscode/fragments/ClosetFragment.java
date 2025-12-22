package com.dresscode.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dresscode.AddClothingActivity;
import com.dresscode.ClothingDetailActivity;
import com.dresscode.adapter.ClothingAdapter;
import com.dresscode.databinding.FragmentClosetBinding;
import com.dresscode.viewmodel.ClothingViewModel;
import com.dresscode.viewmodel.OutfitViewModel;

import static android.content.Context.MODE_PRIVATE;

public class ClosetFragment extends Fragment {

    private FragmentClosetBinding binding;
    private ClothingViewModel clothingViewModel;
    private OutfitViewModel outfitViewModel;
    private ClothingAdapter adapter;
    private int userId;
    private int selectedFilter = 0; // 0=全部, 1=上衣, 2=外套, 3=裤子, 4=裙子, 5=鞋子, 6=配饰
    private String selectedSeason = null; // null=全部；"四季/春/夏/秋/冬"=严格筛选
    private java.util.List<com.dresscode.data.entity.ClothingItem> allItems = new java.util.ArrayList<>();
    private int topCategoryId = -1;
    private int coatCategoryId = -1;
    private int pantsCategoryId = -1;
    private int skirtCategoryId = -1;
    private int shoesCategoryId = -1;
    private int accessoryCategoryId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClosetBinding.inflate(inflater, container, false);
        
        initViewModel();
        setupRecyclerView();
        loadClothingItems();
        setupClickListeners();
        
        return binding.getRoot();
    }

    private void initViewModel() {
        clothingViewModel = new ViewModelProvider(this).get(ClothingViewModel.class);
        outfitViewModel = new ViewModelProvider(this).get(OutfitViewModel.class);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
    }

    private void setupRecyclerView() {
        adapter = new ClothingAdapter();
        binding.rvClothingItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvClothingItems.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(getActivity(), ClothingDetailActivity.class);
            intent.putExtra("clothing_id", item.getId());
            startActivity(intent);
        });
    }

    private void loadClothingItems() {
        if (userId != -1) {
            clothingViewModel.getClothingItemsByUserId(userId).observe(getViewLifecycleOwner(), items -> {
                allItems = (items == null) ? new java.util.ArrayList<>() : items;
                // 同步总衣物统计（始终按全部统计）
                binding.tvTotalCount.setText(String.valueOf(allItems.size()));
                applyFilterAndRender();
            });

            // 读取 categoryId 映射，避免写死 1..6 导致筛选失效
            clothingViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
                if (categories == null) return;
                for (com.dresscode.data.entity.Category c : categories) {
                    if ("上衣".equals(c.getName())) topCategoryId = c.getId();
                    if ("外套".equals(c.getName())) coatCategoryId = c.getId();
                    if ("裤子".equals(c.getName())) pantsCategoryId = c.getId();
                    if ("裙子".equals(c.getName())) skirtCategoryId = c.getId();
                    if ("鞋子".equals(c.getName())) shoesCategoryId = c.getId();
                    if ("配饰".equals(c.getName())) accessoryCategoryId = c.getId();
                }
                applyFilterAndRender();
            });

            // 同步“穿搭数量”（来自我的搭配总数）
            outfitViewModel.getOutfitCount(userId).observe(getViewLifecycleOwner(), c -> {
                int count = (c == null) ? 0 : c;
                binding.tvOutfitCount.setText(String.valueOf(count));
            });
        }
    }

    private void applyFilterAndRender() {
        java.util.List<com.dresscode.data.entity.ClothingItem> filtered = new java.util.ArrayList<>();
        int targetCategoryId = mapFilterToCategoryId(selectedFilter);
        for (com.dresscode.data.entity.ClothingItem item : allItems) {
            // 1) 类别筛选
            boolean passCategory = (selectedFilter == 0) || (targetCategoryId != -1 && item.getCategoryId() == targetCategoryId);
            if (!passCategory) continue;

            // 2) 季节筛选（严格匹配）：选中春/夏/秋/冬时不包含“四季”
            if (selectedSeason != null) {
                String s = item.getSeason();
                boolean passSeason = selectedSeason.equals(s);
                if (!passSeason) continue;
            }

            filtered.add(item);
        }

        if (!filtered.isEmpty()) {
            adapter.setClothingItems(filtered);
            binding.rvClothingItems.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        } else {
            binding.rvClothingItems.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        }
    }

    private int mapFilterToCategoryId(int filter) {
        switch (filter) {
            case 1: return topCategoryId;
            case 2: return coatCategoryId;
            case 3: return pantsCategoryId;
            case 4: return skirtCategoryId;
            case 5: return shoesCategoryId;
            case 6: return accessoryCategoryId;
            default: return -1;
        }
    }

    private void setupClickListeners() {
        binding.fabAddClothing.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddClothingActivity.class);
            startActivity(intent);
        });

        // 分类筛选（不改你现有按钮布局，只加点击逻辑）
        binding.btnAll.setOnClickListener(v -> {
            selectedFilter = 0;
            applyFilterAndRender();
            updateCategoryButtons();
        });
        binding.btnTop.setOnClickListener(v -> {
            selectedFilter = 1;
            applyFilterAndRender();
            updateCategoryButtons();
        });
        binding.btnCoat.setOnClickListener(v -> {
            selectedFilter = 2;
            applyFilterAndRender();
            updateCategoryButtons();
        });
        binding.btnBottom.setOnClickListener(v -> {
            selectedFilter = 3; // 裤子
            applyFilterAndRender();
            updateCategoryButtons();
        });
        binding.btnSkirt.setOnClickListener(v -> {
            selectedFilter = 4; // 裙子
            applyFilterAndRender();
            updateCategoryButtons();
        });
        binding.btnShoes.setOnClickListener(v -> {
            selectedFilter = 5;
            applyFilterAndRender();
            updateCategoryButtons();
        });
        binding.btnAccessory.setOnClickListener(v -> {
            selectedFilter = 6;
            applyFilterAndRender();
            updateCategoryButtons();
        });

        // 季节筛选
        binding.btnSeasonAll.setOnClickListener(v -> {
            selectedSeason = null;
            applyFilterAndRender();
            updateSeasonButtons();
        });
        binding.btnSeasonAllYear.setOnClickListener(v -> {
            selectedSeason = "四季";
            applyFilterAndRender();
            updateSeasonButtons();
        });
        binding.btnSeasonSpring.setOnClickListener(v -> {
            selectedSeason = "春";
            applyFilterAndRender();
            updateSeasonButtons();
        });
        binding.btnSeasonSummer.setOnClickListener(v -> {
            selectedSeason = "夏";
            applyFilterAndRender();
            updateSeasonButtons();
        });
        binding.btnSeasonAutumn.setOnClickListener(v -> {
            selectedSeason = "秋";
            applyFilterAndRender();
            updateSeasonButtons();
        });
        binding.btnSeasonWinter.setOnClickListener(v -> {
            selectedSeason = "冬";
            applyFilterAndRender();
            updateSeasonButtons();
        });

        // 初始选中态
        updateCategoryButtons();
        updateSeasonButtons();
    }

    private void updateCategoryButtons() {
        int pink = requireContext().getResources().getColor(com.dresscode.R.color.primary_pink);
        int white = requireContext().getResources().getColor(com.dresscode.R.color.white);
        int textPrimary = requireContext().getResources().getColor(com.dresscode.R.color.text_primary);

        setFilterButtonSelected(binding.btnAll, selectedFilter == 0, pink, white, textPrimary);
        setFilterButtonSelected(binding.btnTop, selectedFilter == 1, pink, white, textPrimary);
        setFilterButtonSelected(binding.btnCoat, selectedFilter == 2, pink, white, textPrimary);
        setFilterButtonSelected(binding.btnBottom, selectedFilter == 3, pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSkirt, selectedFilter == 4, pink, white, textPrimary);
        setFilterButtonSelected(binding.btnShoes, selectedFilter == 5, pink, white, textPrimary);
        setFilterButtonSelected(binding.btnAccessory, selectedFilter == 6, pink, white, textPrimary);
    }

    private void updateSeasonButtons() {
        int pink = requireContext().getResources().getColor(com.dresscode.R.color.primary_pink);
        int white = requireContext().getResources().getColor(com.dresscode.R.color.white);
        int textPrimary = requireContext().getResources().getColor(com.dresscode.R.color.text_primary);

        setFilterButtonSelected(binding.btnSeasonAll, selectedSeason == null, pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSeasonAllYear, "四季".equals(selectedSeason), pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSeasonSpring, "春".equals(selectedSeason), pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSeasonSummer, "夏".equals(selectedSeason), pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSeasonAutumn, "秋".equals(selectedSeason), pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSeasonWinter, "冬".equals(selectedSeason), pink, white, textPrimary);
    }

    private void setFilterButtonSelected(com.google.android.material.button.MaterialButton b, boolean selected,
                                         int pink, int white, int textPrimary) {
        if (selected) {
            b.setBackgroundTintList(ColorStateList.valueOf(pink));
            b.setTextColor(white);
        } else {
            b.setBackgroundTintList(ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
            b.setTextColor(textPrimary);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 刷新列表
        if (userId != -1) {
            // 重新触发渲染（数据源LiveData会在有变更时自动推送，这里只做兜底）
            applyFilterAndRender();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}