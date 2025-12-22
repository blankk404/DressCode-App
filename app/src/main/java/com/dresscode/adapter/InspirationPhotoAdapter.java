package com.dresscode.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dresscode.R;
import com.dresscode.data.entity.InspirationPhoto;

import java.util.ArrayList;
import java.util.List;

public class InspirationPhotoAdapter extends RecyclerView.Adapter<InspirationPhotoAdapter.VH> {

    private final List<InspirationPhoto> items = new ArrayList<>();
    private OnPhotoLongClickListener longClickListener;

    public interface OnPhotoLongClickListener {
        void onLongClick(InspirationPhoto photo);
    }

    public void setOnPhotoLongClickListener(OnPhotoLongClickListener l) {
        this.longClickListener = l;
    }

    public void setItems(List<InspirationPhoto> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inspiration_photo, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        InspirationPhoto p = items.get(position);
        Glide.with(holder.iv.getContext())
                .load(p.getUri())
                .placeholder(R.drawable.logo_background)
                .into(holder.iv);

        holder.iv.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(p);
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
        ImageView iv;
        VH(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
        }
    }
}


