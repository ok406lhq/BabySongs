package com.cool.music.adapter.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cool.music.R;

import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.PreviewViewHolder> {

    private Context context;
    private List<Integer> photoList;

    public ImagePreviewAdapter(Context context, List<Integer> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_preview, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        int photoRes = photoList.get(position);

        // 使用 Glide 加载高清图片
        Glide.with(context)
                .load(photoRes)
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return photoList != null ? photoList.size() : 0;
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        ImageView photoView;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.preview_photo_view);
        }
    }
}