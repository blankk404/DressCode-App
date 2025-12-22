package com.dresscode.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dresscode.R;

import java.util.ArrayList;
import java.util.List;

public class AiImageAdapter extends RecyclerView.Adapter<AiImageAdapter.VH> {

    private final List<String> urls = new ArrayList<>();

    public void setUrls(List<String> list) {
        urls.clear();
        if (list != null) urls.addAll(list);
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
        String url = urls.get(position);
        Glide.with(holder.iv.getContext())
                .load(url)
                .placeholder(R.drawable.logo_background)
                .into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView iv;
        VH(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
        }
    }
}


