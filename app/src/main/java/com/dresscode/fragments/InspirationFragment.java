package com.dresscode.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.dresscode.databinding.FragmentInspirationBinding;
import com.dresscode.AiRecommendActivity;
import com.dresscode.adapter.InspirationPhotoAdapter;
import com.dresscode.data.entity.InspirationTag;
import com.dresscode.viewmodel.InspirationViewModel;
import com.google.android.material.button.MaterialButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

public class InspirationFragment extends Fragment {

    private FragmentInspirationBinding binding;
    private InspirationViewModel viewModel;
    private InspirationPhotoAdapter photoAdapter;
    private int userId;
    private InspirationTag selectedTag;
    private String lastQuery = null;
    private java.util.List<InspirationTag> cachedTags = new java.util.ArrayList<>();
    private androidx.lifecycle.LiveData<java.util.List<com.dresscode.data.entity.InspirationPhoto>> currentPhotosLiveData;

    private final ActivityResultLauncher<Intent> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    if (selectedTag == null) return;
                    final int takeFlags = (result.getData().getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));

                    // 批量选择：ClipData
                    if (result.getData().getClipData() != null) {
                        android.content.ClipData clip = result.getData().getClipData();
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            if (uri == null) continue;
                            takePersistable(uri, takeFlags);
                            viewModel.addPhoto(selectedTag.getId(), uri.toString());
                        }
                        return;
                    }

                    // 单张选择：data
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        takePersistable(uri, takeFlags);
                        viewModel.addPhoto(selectedTag.getId(), uri.toString());
                    }
                }
            });

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) openPhotoPicker();
                else Toast.makeText(getContext(), "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show();
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInspirationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(InspirationViewModel.class);
        SharedPreferences sp = requireActivity().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        photoAdapter = new InspirationPhotoAdapter();
        binding.rvInspirationPhotos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvInspirationPhotos.setAdapter(photoAdapter);

        // 长按删除图片
        photoAdapter.setOnPhotoLongClickListener(photo -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("删除图片")
                    .setMessage("确定删除这张图片吗？")
                    .setPositiveButton("删除", (d, w) -> viewModel.deletePhoto(photo))
                    .setNegativeButton("取消", null)
                    .show();
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), msg -> {
            if (!TextUtils.isEmpty(msg)) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        setupSearchAndAi();
        setupTagBar();
        setupFab();
    }

    private void setupSearchAndAi() {
        // 1) 键盘搜索键
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch();
                return true;
            }
            return false;
        });

        // 2) 搜索框右侧搜索按钮（endIcon）
        binding.tilSearch.setEndIconOnClickListener(v -> performSearch());

        binding.cardAiRecommend.setOnClickListener(v -> {
            if (TextUtils.isEmpty(lastQuery)) return;
            Intent intent = new Intent(getActivity(), AiRecommendActivity.class);
            intent.putExtra("query", lastQuery);
            startActivity(intent);
        });
    }

    private void performSearch() {
        String q = binding.etSearch.getText() == null ? "" : binding.etSearch.getText().toString().trim();
        lastQuery = q;
        if (!TextUtils.isEmpty(q)) {
            binding.cardAiRecommend.setVisibility(View.VISIBLE);
            binding.tvAiTitle.setText("AI推荐：" + q);
            binding.tvAiSub.setText("点击查看10张推荐 · 可换一批");
        } else {
            binding.cardAiRecommend.setVisibility(View.GONE);
        }
    }

    private void setupTagBar() {
        viewModel.getTags(userId).observe(getViewLifecycleOwner(), tags -> {
            cachedTags = (tags == null) ? new java.util.ArrayList<>() : tags;

            if (cachedTags.isEmpty()) {
                selectedTag = null;
                renderTagButtons();
                photoAdapter.setItems(null);
                binding.tvInspirationEmpty.setVisibility(View.VISIBLE);
                return;
            }

            if (selectedTag == null) selectedTag = cachedTags.get(0);
            // 如果当前选中的 tag 已不存在，回退到第一个
            boolean exists = false;
            for (InspirationTag t : cachedTags) {
                if (selectedTag != null && t.getId() == selectedTag.getId()) { exists = true; break; }
            }
            if (!exists) selectedTag = cachedTags.get(0);

            renderTagButtons();
            observePhotos();
        });
    }

    private void renderTagButtons() {
        binding.tagContainer.removeAllViews();

        // “+” 新建按钮（统一为 MaterialButton 风格）
        MaterialButton add = new MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        add.setText("+");
        add.setMinHeight(dp(36));
        add.setInsetTop(0);
        add.setInsetBottom(0);
        add.setCornerRadius(dp(18));
        setButtonMargin(add);
        add.setOnClickListener(v -> promptAddTag());
        binding.tagContainer.addView(add);

        for (InspirationTag tag : cachedTags) {
            boolean selected = selectedTag != null && tag.getId() == selectedTag.getId();

            MaterialButton b = new MaterialButton(requireContext(), null,
                    selected ? com.google.android.material.R.attr.materialButtonStyle
                            : com.google.android.material.R.attr.materialButtonOutlinedStyle);
            b.setText(tag.getName());
            b.setMinHeight(dp(36));
            b.setInsetTop(0);
            b.setInsetBottom(0);
            b.setCornerRadius(dp(18));
            setButtonMargin(b);

            if (selected) {
                b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        requireContext().getResources().getColor(com.dresscode.R.color.primary_pink)));
                b.setTextColor(requireContext().getResources().getColor(com.dresscode.R.color.white));
            }

            b.setOnClickListener(v -> {
                selectedTag = tag;
                renderTagButtons();
                observePhotos();
            });
            b.setOnLongClickListener(v -> {
                promptEditOrDeleteTag(tag);
                return true;
            });
            binding.tagContainer.addView(b);
        }
    }

    private void observePhotos() {
        if (selectedTag == null) return;
        if (currentPhotosLiveData != null) {
            currentPhotosLiveData.removeObservers(getViewLifecycleOwner());
        }
        currentPhotosLiveData = viewModel.getPhotosByTag(selectedTag.getId());
        currentPhotosLiveData.observe(getViewLifecycleOwner(), photos -> {
            photoAdapter.setItems(photos);
            binding.tvInspirationEmpty.setVisibility((photos == null || photos.isEmpty()) ? View.VISIBLE : View.GONE);
        });
    }

    private void setupFab() {
        binding.fabAddInspiration.setOnClickListener(v -> {
            if (selectedTag == null) {
                Toast.makeText(getContext(), "请先创建一个标签", Toast.LENGTH_SHORT).show();
                return;
            }
            checkPermissionAndPick();
        });
    }

    private void promptAddTag() {
        EditText et = new EditText(getContext());
        et.setHint("输入标签名称");
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("新建灵感标签")
                .setView(et)
                .setPositiveButton("创建", (d, w) -> {
                    String name = et.getText().toString().trim();
                    if (!TextUtils.isEmpty(name)) viewModel.addTag(userId, name);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void promptEditOrDeleteTag(InspirationTag tag) {
        String[] items = {"重命名", "删除"};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(tag.getName())
                .setItems(items, (d, which) -> {
                    if (which == 0) {
                        EditText et = new EditText(getContext());
                        et.setText(tag.getName());
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("重命名标签")
                                .setView(et)
                                .setPositiveButton("保存", (d2, w2) -> {
                                    String name = et.getText().toString().trim();
                                    if (!TextUtils.isEmpty(name)) viewModel.updateTag(tag, name);
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else {
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("删除标签")
                                .setMessage("删除后该标签下图片也会一并删除，是否继续？")
                                .setPositiveButton("删除", (d2, w2) -> viewModel.deleteTag(tag))
                                .setNegativeButton("取消", null)
                                .show();
                    }
                })
                .show();
    }

    private void checkPermissionAndPick() {
        String permission = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

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
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        photoPickerLauncher.launch(intent);
    }

    private void takePersistable(Uri uri, int takeFlags) {
        try {
            requireContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int dp(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private void setButtonMargin(View v) {
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMarginEnd(dp(8));
        v.setLayoutParams(lp);
    }
}