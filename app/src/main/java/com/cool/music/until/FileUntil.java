package com.cool.music.until;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Savepoint;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileUntil {

    // ✨✨✨ 添加静态 Context 变量
    private static Context appContext;

    /**
     * ✨✨✨ 初始化 Context（在 Application 或 MainActivity 中调用）
     * @param context 建议使用 Application Context
     */
    public static void init(Context context) {
        if (appContext == null) {
            appContext = context.getApplicationContext();
        }
    }

    /**
     * ✨✨✨ 获取 Context
     * @return Application Context
     */
    public static Context getContext() {
        if (appContext == null) {
            throw new IllegalStateException("FileUntil 未初始化！请先调用 FileUntil.init(context)");
        }
        return appContext;
    }


    /**
     * 生成随机图片名字
     *
     * @return
     */
    public static String getFileName() {
        //  /6666.png
        String pigName = "/" + UUID.randomUUID().toString().replace("-", "") + ".png";
//        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + pigName;
        return getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + pigName;
    }

    public static String getFileName(String hz) {
        //  /6666.png
        String pigName = "/" + UUID.randomUUID().toString().replace("-", "") + "." + hz;
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + pigName;
    }

    public static String getFileName(Context context) {
        String pigName = "/" + UUID.randomUUID().toString().replace("-", "") + ".png";
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + pigName;
    }

    public static String getFileName(Context context, String hz) {
        String pigName = "/" + UUID.randomUUID().toString().replace("-", "") + "." + hz;
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + pigName;
    }

    /**
     * 将2计件制文件保存到png
     *
     * @return
     */
    public static String saveImageBitmapToFileImg(Uri uri, Context context) {
        String path = FileUntil.getFileName();//随机获取一个指定目录文件的路径
        CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                File file = new File(path);//创建文件
                try {
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        };


        //下面是实现保存
        Glide.with(context).asBitmap().load(uri).into(target);


        return path;
    }

    /**
     * 将drawable里面文件，转换并保存到手机里面的真实位置
     * 将apk里面的内容进行释放
     */

    public static String saveDrawableToFileSaveAs(Context context, int id) {
        String path = FileUntil.getFileName();

        Drawable defaultDrawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmapDef = ((BitmapDrawable) defaultDrawable).getBitmap();//获取这个图片的二进制文件
        FileUntil.saveImageBitmapToFileImgEx(bitmapDef, path);//保存图片


        return path;
    }


    /**
     * 将2进制保存图片
     *
     * @param bitmap
     * @param path
     */
    public static void saveImageBitmapToFileImgEx(Bitmap bitmap, String path) {
        File file = new File(path);//创建文件
        try {
            if (bitmap == null) return;

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存音乐
     */
    public static void saveMusic(Context context, int resourceId, String path) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);//获取资源ID
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将drawable里面文件，转换并保存到手机里面的真实位置
     * 将apk里面的内容进行释放
     */

    public static String saveMusicThread(Context context, int id, String musicImg) {
        String path = FileUntil.getFileName(context, "mp3");
        FileUntil.saveMusic(context, id, path);
        Tools.getMusicName(path, musicImg);
        return path;
    }

    /**
     * Uri内容实现保存
     * 将apk里面的内容进行释放
     */

    public static String saveMusicThread(Context context, Uri uri, String musicImg) {
        String path = FileUntil.getFileName(context, "mp3");
        FileUntil.saveMusic(context, uri, path);
        Tools.getMusicName(path, musicImg);
        return path;
    }

    /**
     * 保存音乐
     */
    public static void saveMusic(Context context, Uri uri, String path) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
