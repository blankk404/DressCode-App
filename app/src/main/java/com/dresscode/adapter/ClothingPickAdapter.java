package com.dresscode.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dresscode.R;
import com.dresscode.data.entity.ClothingItem;
import com.dresscode.utils.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 衣物选择（多选）适配器
 */
public class ClothingPickAdapter extends RecyclerView.Adapter<ClothingPickAdapter.VH> {

    private final List<ClothingItem> items = new ArrayList<>();
    private final Set<Integer> selectedIds = new HashSet<>();

    public void setItems(List<ClothingItem> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public List<ClothingItem> getSelectedItems() {
        List<ClothingItem> selected = new ArrayList<>();
        for (ClothingItem item : items) {
            if (selectedIds.contains(item.getId())) selected.add(item);
        }
        return selected;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clothing_pick, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ClothingItem item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvSub.setText(item.getSeason());
        ImageLoader.load(holder.iv, item.getImagePath(), R.drawable.logo_background);

        boolean checked = selectedIds.contains(item.getId());
        holder.itemView.setAlpha(checked ? 0.75f : 1f);
        holder.checkOverlay.setVisibility(checked ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (selectedIds.contains(item.getId())) selectedIds.remove(item.getId());
            else selectedIds.add(item.getId());
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tvName;
        TextView tvSub;
        FrameLayout checkOverlay;

        VH(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.ivClothingImage);
            tvName = itemView.findViewById(R.id.tvClothingName);
            tvSub = itemView.findViewById(R.id.tvClothingCategory);
            checkOverlay = itemView.findViewById(R.id.checkOverlay);
        }
    }
}


