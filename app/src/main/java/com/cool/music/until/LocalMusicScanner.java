package com.cool.music.until;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.cool.music.bean.MusicBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地音乐扫描工具类
 * 用于扫描设备中的MP3文件并提取元数据
 */
public class LocalMusicScanner {
    private static final String TAG = "LocalMusicScanner";

    /**
     * 扫描设备中的所有MP3音乐文件
     *
     * @param context 上下文
     * @return 扫描到的音乐列表
     */
    public static List<MusicBean> scanLocalMusic(Context context) {
        List<MusicBean> musicList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        // 定义需要查询的列
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM
        };

        // 查询条件：只查询MP3文件且时长大于30秒（过滤掉提示音等短音频）
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
                MediaStore.Audio.Media.DURATION + " > 30000";

        // 按标题排序
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        try (Cursor cursor = contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
        )) {
            if (cursor != null && cursor.getCount() > 0) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);

                while (cursor.moveToNext()) {
                    try {
                        String id = "local_" + cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        String artist = cursor.getString(artistColumn);
                        String path = cursor.getString(dataColumn);
                        long duration = cursor.getLong(durationColumn);

                        // 处理未知艺术家
                        if (artist == null || artist.equals("<unknown>") || artist.trim().isEmpty()) {
                            artist = "未知艺术家";
                        }

                        // 处理未知标题
                        if (title == null || title.trim().isEmpty()) {
                            // 从文件路径提取文件名
                            String fileName = path.substring(path.lastIndexOf("/") + 1);
                            if (fileName.endsWith(".mp3")) {
                                title = fileName.substring(0, fileName.length() - 4);
                            } else {
                                title = fileName;
                            }
                        }

                        // 创建MusicBean对象
                        MusicBean music = new MusicBean();
                        music.setId(id);
                        music.setName(title);
                        music.setSinger(artist);
                        music.setPath(path);
                        music.setImg(""); // 本地音乐暂时不设置封面，可以后续扩展
                        music.setGenre_id(""); // 本地音乐暂不设置曲风
                        music.setCreate_time(String.valueOf(System.currentTimeMillis()));

                        musicList.add(music);
                        Log.d(TAG, "Found music: " + title + " - " + artist);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing music item", e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error scanning local music", e);
        }

        Log.d(TAG, "Total music files found: " + musicList.size());
        return musicList;
    }

    /**
     * 使用MediaMetadataRetriever获取详细的音乐元数据
     * 此方法可用于获取更详细的信息，但速度较慢
     *
     * @param path 音乐文件路径
     * @return MusicBean对象，如果解析失败返回null
     */
    public static MusicBean extractMetadata(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

            // 处理空值
            if (title == null || title.trim().isEmpty()) {
                String fileName = path.substring(path.lastIndexOf("/") + 1);
                title = fileName.endsWith(".mp3") ? fileName.substring(0, fileName.length() - 4) : fileName;
            }

            if (artist == null || artist.trim().isEmpty()) {
                artist = "未知艺术家";
            }

            MusicBean music = new MusicBean();
            music.setId("local_" + System.currentTimeMillis() + "_" + path.hashCode());
            music.setName(title);
            music.setSinger(artist);
            music.setPath(path);
            music.setImg(""); // 可以尝试提取专辑封面
            music.setGenre_id("");
            music.setCreate_time(String.valueOf(System.currentTimeMillis()));

            return music;
        } catch (Exception e) {
            Log.e(TAG, "Error extracting metadata from: " + path, e);
            return null;
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing retriever", e);
            }
        }
    }

    /**
     * 从MP3文件中提取专辑封面
     * 返回Base64编码的图片字符串，可用于数据库存储
     *
     * @param path 音乐文件路径
     * @return Base64编码的封面图片，如果没有封面返回null
     */
    public static String extractAlbumArt(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(path);
            byte[] art = retriever.getEmbeddedPicture();
            
            if (art != null) {
                // 将字节数组转换为Base64字符串
                return android.util.Base64.encodeToString(art, android.util.Base64.DEFAULT);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting album art from: " + path, e);
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                Log.e(TAG, "Error releasing retriever", e);
            }
        }
        return null;
    }

    /**
     * 格式化时长（毫秒转为分:秒）
     *
     * @param durationMs 时长（毫秒）
     * @return 格式化后的时长字符串，如 "3:45"
     */
    public static String formatDuration(long durationMs) {
        long seconds = (durationMs / 1000) % 60;
        long minutes = (durationMs / (1000 * 60)) % 60;
        long hours = durationMs / (1000 * 60 * 60);

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
}
