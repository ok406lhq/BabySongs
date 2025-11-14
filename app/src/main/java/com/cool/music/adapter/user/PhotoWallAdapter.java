package com.cool.music.adapter.user;

/**
 * 包名：com.cool.music.adapter.user
 * 创建人: Latte
 * 创建时间： 2025/11/14 16:52
 * 描述：
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.activity.user.ImagePreviewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhotoWallAdapter extends RecyclerView.Adapter<PhotoWallAdapter.PhotoViewHolder> {

    private Context context;
    private List<Integer> photoList;
    private Random random = new Random();

    public PhotoWallAdapter(Context context, List<Integer> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo_wall, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        int photoRes = photoList.get(position);

        // ✨ 使用 Glide 加载图片
        Glide.with(context).load(photoRes).apply(RequestOptions.bitmapTransform(new RoundedCorners(24))).placeholder(R.drawable.icon_gd) // 占位图
                .error(R.drawable.icon_gd) // 错误图
                .into(holder.photoImageView);

        // ✨ 设置随机高度，实现瀑布流效果
        ViewGroup.LayoutParams params = holder.photoImageView.getLayoutParams();
        params.height = getRandomHeight();
        holder.photoImageView.setLayoutParams(params);

        // ✨✨✨ 点击图片进入全屏预览
        holder.photoImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImagePreviewActivity.class);
            intent.putExtra("position", position);
            intent.putIntegerArrayListExtra("photo_list", new ArrayList<>(photoList));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return photoList != null ? photoList.size() : 0;
    }

    /**
     * 生成随机高度，制造瀑布流效果
     */
    private int getRandomHeight() {
        // 随机高度在 400-800 像素之间
        return 400 + random.nextInt(400);
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photo_image_view);
        }
    }
}
