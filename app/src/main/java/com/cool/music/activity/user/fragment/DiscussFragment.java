package com.cool.music.activity.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.activity.user.PostDetailActivity;
import com.cool.music.activity.user.RunMusicDetailActivity;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PostBean;
import com.cool.music.dao.CommentDao;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.PostDao;
import com.cool.music.dao.UserDao;

import java.util.List;

/**
 * 讨论区Fragment
 */
public class DiscussFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_discuss, container, false);

        recyclerView = rootview.findViewById(R.id.discuss_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPosts();

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    private void loadPosts() {
        List<PostBean> posts = PostDao.getAllPosts();
        adapter = new PostAdapter(posts);
        recyclerView.setAdapter(adapter);
    }

    // 简化的帖子适配器
    class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
        private List<PostBean> posts;

        PostAdapter(List<PostBean> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PostBean post = posts.get(position);

            // 获取歌曲信息
            MusicBean music = MusicDao.getMusicById(post.getMusic_id());
            if (music != null) {
                holder.musicTag.setText("@" + music.getName());
                holder.musicTag.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), RunMusicDetailActivity.class);
                    intent.putExtra("musicId", music.getId());
                    startActivity(intent);
                });
            }

            // 获取用户昵称
            String nickname = UserDao.getUserNickname(post.getUser_id());
            holder.username.setText(nickname != null ? nickname : post.getUser_id());

            holder.time.setText(post.getCreate_time());
            holder.content.setText(post.getContent());

            // 获取评论数量
            int commentCount = CommentDao.getCommentsByPostId(post.getId()).size();
            holder.commentCount.setText(commentCount + "条评论");

            // 点击整个帖子跳转到详情页
            holder.container.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), PostDetailActivity.class);
                intent.putExtra("postId", post.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return posts != null ? posts.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView musicTag, username, time, content, commentCount;
            LinearLayout container;

            ViewHolder(View view) {
                super(view);
                musicTag = view.findViewById(R.id.post_music_tag);
                username = view.findViewById(R.id.post_username);
                time = view.findViewById(R.id.post_time);
                content = view.findViewById(R.id.post_content);
                commentCount = view.findViewById(R.id.post_comment_count);
                container = view.findViewById(R.id.post_item_container);
            }
        }
    }
}
