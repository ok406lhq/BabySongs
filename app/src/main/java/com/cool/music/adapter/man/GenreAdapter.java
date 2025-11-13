package com.cool.music.adapter.man;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.bean.GenreBean;

import java.util.List;

/**
 * 曲风分类列表适配器
 */
public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private List<GenreBean> list;
    private OnGenreClickListener listener;
    private int selectedPosition = 0;  // 默认选中第一个

    public interface OnGenreClickListener {
        void onGenreClick(GenreBean genre, int position);
    }

    public GenreAdapter(List<GenreBean> list, OnGenreClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GenreBean genre = list.get(position);
        holder.name.setText(genre.getName());

        // 设置选中状态的背景颜色
        if (selectedPosition == position) {
            holder.container.setBackgroundColor(Color.WHITE);
            holder.name.setTextColor(Color.parseColor("#333333"));
        } else {
            holder.container.setBackgroundColor(Color.parseColor("#F5F5F5"));
            holder.name.setTextColor(Color.parseColor("#666666"));
        }

        holder.container.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onGenreClick(genre, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        LinearLayout container;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = itemView.findViewById(R.id.genre_item_name);
            container = itemView.findViewById(R.id.genre_item_container);
        }
    }
}
