package com.cool.music.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.bean.MusicBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 本地音乐列表适配器
 * 用于显示扫描到的本地音乐，并支持多选
 */
public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {
    private List<MusicBean> musicList;
    private Set<String> selectedIds; // 记录选中的音乐ID

    public LocalMusicAdapter(List<MusicBean> musicList) {
        this.musicList = musicList != null ? musicList : new ArrayList<>();
        this.selectedIds = new HashSet<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_local_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicBean music = musicList.get(position);
        
        holder.tvTitle.setText(music.getName());
        holder.tvArtist.setText(music.getSinger());
        holder.tvPath.setText(music.getPath());
        
        // 设置复选框状态
        holder.checkbox.setChecked(selectedIds.contains(music.getId()));
        
        // 复选框点击事件
        holder.checkbox.setOnClickListener(v -> {
            if (holder.checkbox.isChecked()) {
                selectedIds.add(music.getId());
            } else {
                selectedIds.remove(music.getId());
            }
        });
        
        // 整行点击切换选中状态
        holder.itemView.setOnClickListener(v -> {
            holder.checkbox.setChecked(!holder.checkbox.isChecked());
            if (holder.checkbox.isChecked()) {
                selectedIds.add(music.getId());
            } else {
                selectedIds.remove(music.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    /**
     * 获取选中的音乐列表
     */
    public List<MusicBean> getSelectedMusic() {
        List<MusicBean> selected = new ArrayList<>();
        for (MusicBean music : musicList) {
            if (selectedIds.contains(music.getId())) {
                selected.add(music);
            }
        }
        return selected;
    }

    /**
     * 全选
     */
    public void selectAll() {
        for (MusicBean music : musicList) {
            selectedIds.add(music.getId());
        }
        notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void deselectAll() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取选中数量
     */
    public int getSelectedCount() {
        return selectedIds.size();
    }

    /**
     * 更新音乐列表
     */
    public void updateMusicList(List<MusicBean> newList) {
        this.musicList = newList != null ? newList : new ArrayList<>();
        this.selectedIds.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvArtist;
        TextView tvPath;
        CheckBox checkbox;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_music_title);
            tvArtist = itemView.findViewById(R.id.tv_music_artist);
            tvPath = itemView.findViewById(R.id.tv_music_path);
            checkbox = itemView.findViewById(R.id.checkbox_select);
        }
    }
}
