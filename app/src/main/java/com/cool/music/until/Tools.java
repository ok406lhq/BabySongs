package com.cool.music.until;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.cool.music.MainActivity;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

public class Tools {

    /**
     * 这是一个弹窗的方法
     * @param context
     * @param text
     */
    public static void Toast(Context context,String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取Mp3里面的封面
     * @param filePath
     * @return
     */
    public static  Bitmap getCoverArt(String filePath) {
        try {
            Mp3File mp3File = new Mp3File(filePath);
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                if (id3v2Tag.getAlbumImage() != null) {
                    byte[] imageData = id3v2Tag.getAlbumImage();
                    return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据音乐的路径将内容保存，并且返回保存的图片路径
     * @param path
     * @return
     */
    public static String getMusicName(String path,String imgPath){
        Bitmap bit = getCoverArt(path);
        FileUntil.saveImageBitmapToFileImgEx(bit, imgPath);
        return imgPath;
    }



    /**
     * 获取当前账号
     * @param context
     * @return
     */
    public static  String getOnAccount(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String businessId=sharedPreferences.getString("account","admin");//如果这个值没有添加则使用默认的
        return businessId;
    }

    /**
     * 获取当前账号
     * @param context
     * @return
     */
    public static  void addPrePreferencesData(Context context,String key,String value){
        SharedPreferences sharedPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key,value);
        edit.apply();
    }


    public static  String getPrePreferencesData(Context context,String key){
        SharedPreferences sharedPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
        String businessId=sharedPreferences.getString(key,null);//如果这个值没有添加则使用默认的
        return businessId;
    }

}
