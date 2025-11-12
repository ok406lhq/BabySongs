package com.example.babysongs.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babysongs.R;
import com.example.babysongs.model.Song;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    private TextView songTitleTextView;
    private TextView currentTimeTextView;
    private TextView totalTimeTextView;
    private SeekBar seekBar;
    private ImageButton playPauseButton;

    private MediaPlayer mediaPlayer;
    private Song currentSong;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Initialize views
        songTitleTextView = findViewById(R.id.song_title_text_view);
        currentTimeTextView = findViewById(R.id.current_time_text_view);
        totalTimeTextView = findViewById(R.id.total_time_text_view);
        seekBar = findViewById(R.id.seek_bar);
        playPauseButton = findViewById(R.id.play_pause_button);

        // Get song from intent
        currentSong = (Song) getIntent().getSerializableExtra("song");
        if (currentSong == null) {
            Toast.makeText(this, "Song not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up player
        setupPlayer();
        playPauseButton.setOnClickListener(v -> togglePlayPause());
        setupSeekBar();
    }

    private void setupPlayer() {
        songTitleTextView.setText(currentSong.getTitle());
        mediaPlayer = MediaPlayer.create(this, currentSong.getResourceId());

        mediaPlayer.setOnPreparedListener(mp -> {
            totalTimeTextView.setText(formatDuration(mediaPlayer.getDuration()));
            seekBar.setMax(mediaPlayer.getDuration());
            playSong();
        });

        mediaPlayer.setOnCompletionListener(mp -> {
            playPauseButton.setImageResource(R.drawable.ic_play_arrow);
            seekBar.setProgress(0);
            currentTimeTextView.setText("0:00");
        });
    }

    private void togglePlayPause() {
        if (mediaPlayer.isPlaying()) {
            pauseSong();
        } else {
            playSong();
        }
    }

    private void playSong() {
        mediaPlayer.start();
        playPauseButton.setImageResource(R.drawable.ic_pause);
        PlayerActivity.this.runOnUiThread(updateSeekBar);
    }

    private void pauseSong() {
        mediaPlayer.pause();
        playPauseButton.setImageResource(R.drawable.ic_play_arrow);
        handler.removeCallbacks(updateSeekBar);
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    currentTimeTextView.setText(formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause updates while user is dragging
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume updates when user releases
                if (mediaPlayer.isPlaying()) {
                    handler.post(updateSeekBar);
                }
            }
        });
    }

    private Runnable updateSeekBar = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                currentTimeTextView.setText(formatDuration(currentPosition));
                handler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    private String formatDuration(long duration) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
