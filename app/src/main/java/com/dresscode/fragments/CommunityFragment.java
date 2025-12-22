package com.dresscode.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dresscode.AddOutfitActivity;
import com.dresscode.adapter.OutfitAdapter;
import com.dresscode.databinding.FragmentMyOutfitsBinding;
import com.dresscode.viewmodel.OutfitViewModel;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

public class CommunityFragment extends Fragment {

    private FragmentMyOutfitsBinding binding;
    private OutfitViewModel outfitViewModel;
    private OutfitAdapter adapter;
    private int userId;
    private String selectedScene = null; // null=全部
    private String selectedSeason = null; // null=全部；"四季/春/夏/秋/冬"=严格筛选
    private int pendingUploadOutfitId = -1;
    private androidx.lifecycle.LiveData<java.util.List<com.dresscode.data.entity.Outfit>> currentOutfitsLiveData;

    private final ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null && pendingUploadOutfitId != -1) {
                        try {
                            final int takeFlags = (result.getData().getFlags()
                                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                            requireContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
                        } catch (Exception ignored) {
                        }
                        outfitViewModel.updateWearPhoto(pendingUploadOutfitId, uri.toString());
                    }
                }
                pendingUploadOutfitId = -1;
            });

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openPhotoPicker();
                } else {
                    Toast.makeText(getContext(), "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentMyOutfitsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        outfitViewModel = new ViewModelProvider(this).get(OutfitViewModel.class);
        SharedPreferences sp = requireActivity().getSharedPreferences("user_prefs",
                android.content.Context.MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        adapter = new OutfitAdapter();
        binding.rvOutfits.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvOutfits.setAdapter(adapter);

        adapter.setOnUploadWearPhotoClickListener(outfitId -> {
            pendingUploadOutfitId = outfitId;
            checkPermissionAndPickPhoto();
        });

        adapter.setOnOpenDetailListener(outfitId -> {
            Intent intent = new Intent(getActivity(), com.dresscode.OutfitDetailActivity.class);
            intent.putExtra("outfit_id", outfitId);
            startActivity(intent);
        });

        adapter.setOnOutfitLongClickListener(outfit -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("删除搭配")
                    .setMessage("确定删除该搭配吗？")
                    .setPositiveButton("删除", (d, w) -> outfitViewModel.deleteOutfit(outfit.getId()))
                    .setNegativeButton("取消", null)
                    .show();
        });

        binding.fabAddOutfit.setOnClickListener(v1 -> {
            startActivity(new Intent(getActivity(), AddOutfitActivity.class));
        });

        setupFilters();
        observeOutfits();
    }

    private void setupFilters() {
        // 场景
        binding.btnSceneAll.setOnClickListener(v -> {
            selectedScene = null;
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSceneWork.setOnClickListener(v -> {
            selectedScene = "工作";
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSceneLeisure.setOnClickListener(v -> {
            selectedScene = "休闲";
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSceneSport.setOnClickListener(v -> {
            selectedScene = "运动";
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSceneDate.setOnClickListener(v -> {
            selectedScene = "约会";
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSceneTravel.setOnClickListener(v -> {
            selectedScene = "旅行";
            observeOutfits();
            updateFilterButtons();
        });

        // 季节
        binding.btnSeasonAll.setOnClickListener(v -> {
            selectedSeason = null;
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSeasonAllYear.setOnClickListener(v -> {
            selectedSeason = "四季";
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSeasonSpring.setOnClickListener(v -> {
            selectedSeason = "春";
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSeasonSummer.setOnClickListener(v -> {
            selectedSeason = "夏";
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSeasonAutumn.setOnClickListener(v -> {
            selectedSeason = "秋";
            observeOutfits();
            updateFilterButtons();
        });
        binding.btnSeasonWinter.setOnClickListener(v -> {
            selectedSeason = "冬";
            observeOutfits();
            updateFilterButtons();
        });

        // 初始选中态
        updateFilterButtons();
    }

    private void observeOutfits() {
        if (userId == -1)
            return;
        if (currentOutfitsLiveData != null) {
            currentOutfitsLiveData.removeObservers(getViewLifecycleOwner());
        }
        currentOutfitsLiveData = outfitViewModel.getOutfits(userId, selectedScene, selectedSeason);
        currentOutfitsLiveData.observe(getViewLifecycleOwner(), outfits -> {
                    if (outfits != null && !outfits.isEmpty()) {
                        adapter.setItems(outfits);
                        binding.rvOutfits.setVisibility(View.VISIBLE);
                        binding.tvEmpty.setVisibility(View.GONE);
                    } else {
                        binding.rvOutfits.setVisibility(View.GONE);
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    }
        });
    }

    private void updateFilterButtons() {
        int pink = requireContext().getResources().getColor(com.dresscode.R.color.primary_pink);
        int white = requireContext().getResources().getColor(com.dresscode.R.color.white);
        int textPrimary = requireContext().getResources().getColor(com.dresscode.R.color.text_primary);

        // scene
        setFilterButtonSelected(binding.btnSceneAll, selectedScene == null, pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSceneWork, "工作".equals(selectedScene), pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSceneLeisure, "休闲".equals(selectedScene), pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSceneSport, "运动".equals(selectedScene), pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSceneDate, "约会".equals(selectedScene), pink, white, textPrimary);
        setFilterButtonSelected(binding.btnSceneTravel, "旅行".equals(selectedScene), pink, white, textPrimary);

        // season
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

    private void checkPermissionAndPickPhoto() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            openPhotoPicker();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openPhotoPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        photoPickerLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}