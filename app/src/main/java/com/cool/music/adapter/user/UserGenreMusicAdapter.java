package com.cool.music.adapter.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.activity.user.RunMusicDetailActivity;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.until.Tools;

import java.util.List;

/**
 * 用户曲库歌曲列表适配器
 */
public class UserGenreMusicAdapter extends RecyclerView.Adapter<UserGenreMusicAdapter.ViewHolder> {

    private List<MusicBean> list;

    public UserGenreMusicAdapter(List<MusicBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.list_user_genre_music, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicBean music = list.get(position);

        holder.index.setText(String.valueOf(position + 1));

        Glide.with(holder.view)
                .load(music.getImg())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                .into(holder.img);

        String title = music.getName() + "-" + music.getSinger();
        holder.title.setText(title);

        holder.container.setOnClickListener(v -> {
            // 将歌曲添加到播放列表
            String account = Tools.getOnAccount(holder.view.getContext());
            PlayMusicDao.insertPlayMusic(account, music.getId());

            // 跳转到播放详情页面
            Intent intent = new Intent(holder.view.getContext(), RunMusicDetailActivity.class);
            intent.putExtra("musicId", music.getId());
            holder.view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView index;
        TextView title;
        ImageView img;
        LinearLayout container;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            index = itemView.findViewById(R.id.user_genre_music_item_index);
            img = itemView.findViewById(R.id.user_genre_music_item_img);
            title = itemView.findViewById(R.id.user_genre_music_item_title);
            container = itemView.findViewById(R.id.user_genre_music_item_container);
        }
    }
}
