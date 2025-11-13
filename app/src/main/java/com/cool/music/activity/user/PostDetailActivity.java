package com.cool.music.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.bean.CommentBean;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PostBean;
import com.cool.music.dao.CommentDao;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.PostDao;
import com.cool.music.dao.UserDao;
import com.cool.music.until.Tools;

import java.util.List;
import java.util.UUID;

/**
 * 帖子详情Activity
 */
public class PostDetailActivity extends AppCompatActivity {

    private String postId;
    private PostBean post;
    private TextView musicTag, username, time, content;
    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private Button commentSend;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 获取帖子ID
        postId = getIntent().getStringExtra("postId");
        if (postId == null) {
            Tools.Toast(this, "帖子不存在");
            finish();
            return;
        }

        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.post_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 初始化视图
        musicTag = findViewById(R.id.post_detail_music_tag);
        username = findViewById(R.id.post_detail_username);
        time = findViewById(R.id.post_detail_time);
        content = findViewById(R.id.post_detail_content);
        commentsRecyclerView = findViewById(R.id.post_detail_comments);
        commentInput = findViewById(R.id.post_detail_comment_input);
        commentSend = findViewById(R.id.post_detail_comment_send);

        // 加载帖子信息
        loadPost();

        // 加载评论
        loadComments();

        // 发送评论
        commentSend.setOnClickListener(v -> sendComment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadComments();
    }

    private void loadPost() {
        post = PostDao.getPostById(postId);
        if (post == null) {
            Tools.Toast(this, "帖子不存在");
            finish();
            return;
        }

        // 获取歌曲信息
        MusicBean music = MusicDao.getMusicById(post.getMusic_id());
        if (music != null) {
            musicTag.setText("@" + music.getName());
            musicTag.setOnClickListener(v -> {
                Intent intent = new Intent(this, RunMusicDetailActivity.class);
                intent.putExtra("musicId", music.getId());
                startActivity(intent);
            });
        }

        // 获取用户昵称
        String nickname = UserDao.getUserNickname(post.getUser_id());
        username.setText(nickname != null ? nickname : post.getUser_id());

        time.setText(post.getCreate_time());
        content.setText(post.getContent());
    }

    private void loadComments() {
        List<CommentBean> comments = CommentDao.getCommentsByPostId(postId);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(comments);
        commentsRecyclerView.setAdapter(commentAdapter);
    }

    private void sendComment() {
        String commentContent = commentInput.getText().toString().trim();
        if (commentContent.isEmpty()) {
            Tools.Toast(this, "请输入评论内容");
            return;
        }

        String commentId = UUID.randomUUID().toString().replace("-", "");
        String userId = Tools.getOnAccount(this);

        int result = CommentDao.addComment(commentId, postId, userId, commentContent);
        if (result > 0) {
            Tools.Toast(this, "评论成功");
            commentInput.setText("");
            loadComments(); // 刷新评论列表
        } else {
            Tools.Toast(this, "评论失败");
        }
    }

    // 评论适配器
    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
        private List<CommentBean> comments;

        CommentAdapter(List<CommentBean> comments) {
            this.comments = comments;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CommentBean comment = comments.get(position);

            // 获取用户昵称
            String nickname = UserDao.getUserNickname(comment.getUser_id());
            holder.username.setText(nickname != null ? nickname : comment.getUser_id());

            holder.time.setText(comment.getCreate_time());
            holder.content.setText(comment.getContent());
        }

        @Override
        public int getItemCount() {
            return comments != null ? comments.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView username, time, content;

            ViewHolder(View view) {
                super(view);
                username = view.findViewById(R.id.comment_username);
                time = view.findViewById(R.id.comment_time);
                content = view.findViewById(R.id.comment_content);
            }
        }
    }
}
