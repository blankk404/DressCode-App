package com.dresscode.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dresscode.R;
import com.dresscode.data.entity.ClothingItem;

import java.util.ArrayList;
import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private List<ClothingItem> clothingItems = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ClothingItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setClothingItems(List<ClothingItem> items) {
        this.clothingItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClothingItem item = clothingItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return clothingItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivClothingImage;
        TextView tvClothingName;
        TextView tvClothingCategory;

        ViewHolder(View itemView) {
            super(itemView);
            ivClothingImage = itemView.findViewById(R.id.ivClothingImage);
            tvClothingName = itemView.findViewById(R.id.tvClothingName);
            tvClothingCategory = itemView.findViewById(R.id.tvClothingCategory);
        }

        void bind(ClothingItem item, OnItemClickListener listener) {
            tvClothingName.setText(item.getName());
            tvClothingCategory.setText(item.getSeason());

            if (!TextUtils.isEmpty(item.getImagePath())) {
                try {
                    Uri imageUri = Uri.parse(item.getImagePath());
                    ivClothingImage.setImageURI(imageUri);
                } catch (Exception e) {
                    ivClothingImage.setImageResource(R.drawable.logo_background);
                }
            } else {
                ivClothingImage.setImageResource(R.drawable.logo_background);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}

