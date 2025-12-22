package com.dresscode.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dresscode.R;
import com.dresscode.OutfitDetailActivity;
import com.dresscode.data.entity.Outfit;
import com.dresscode.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.VH> {
    private OnOpenDetailListener detailListener;
    private OnOutfitLongClickListener longClickListener;

    private final List<Outfit> items = new ArrayList<>();
    private OnUploadWearPhotoClickListener uploadListener;

    public interface OnUploadWearPhotoClickListener {
        void onUpload(int outfitId);
    }

    public interface OnOpenDetailListener {
        void onOpen(int outfitId);
    }

    public interface OnOutfitLongClickListener {
        void onLongClick(Outfit outfit);
    }

    public void setOnUploadWearPhotoClickListener(OnUploadWearPhotoClickListener l) {
        this.uploadListener = l;
    }

    public void setOnOpenDetailListener(OnOpenDetailListener l) {
        this.detailListener = l;
    }

    public void setOnOutfitLongClickListener(OnOutfitLongClickListener l) {
        this.longClickListener = l;
    }

    public void setItems(List<Outfit> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outfit, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Outfit outfit = items.get(position);

        // 调整卡片高度，使左右两图均为 3:4（每半宽度 * 4/3 => 总高度 = 宽度 * 2/3）
        holder.itemView.post(() -> {
            int w = holder.itemView.getWidth();
            if (w > 0) {
                ViewGroup.LayoutParams lp = holder.container.getLayoutParams();
                int targetH = (w * 2) / 3;
                if (lp != null && lp.height != targetH) {
                    lp.height = targetH;
                    holder.container.setLayoutParams(lp);
                }
            }
        });

        ImageLoader.load(holder.ivCollage, outfit.getCollagePath(), R.drawable.logo_background);

        boolean hasWear = !TextUtils.isEmpty(outfit.getWearPhotoUri());
        holder.ivWearPhoto.setVisibility(hasWear ? View.VISIBLE : View.GONE);
        holder.placeholder.setVisibility(hasWear ? View.GONE : View.VISIBLE);
        if (hasWear) {
            ImageLoader.load(holder.ivWearPhoto, outfit.getWearPhotoUri(), R.drawable.logo_background);
        }

        holder.rightPanel.setOnClickListener(v -> {
            if (uploadListener != null) {
                uploadListener.onUpload(outfit.getId());
            }
        });

        holder.ivCollage.setOnClickListener(v -> {
            if (detailListener != null) {
                detailListener.onOpen(outfit.getId());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(outfit);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        View container;
        ImageView ivCollage;
        FrameLayout rightPanel;
        ImageView ivWearPhoto;
        LinearLayout placeholder;

        VH(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.outfitContainer);
            ivCollage = itemView.findViewById(R.id.ivCollage);
            rightPanel = itemView.findViewById(R.id.rightPanel);
            ivWearPhoto = itemView.findViewById(R.id.ivWearPhoto);
            placeholder = itemView.findViewById(R.id.placeholder);
        }
    }
}


