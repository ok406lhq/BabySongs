package com.cool.music;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.net.Uri;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cool.music.activity.user.UserManageActivity;
import com.cool.music.until.DBUntil;
import com.cool.music.until.Tools;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1001;
    private static final int REQUEST_MANAGE_STORAGE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 检查并请求存储权限
        if (checkStoragePermission()) {
            initApp();
        } else {
            requestStoragePermission();
        }
    }

    /**
     * 检查存储权限
     */
    private boolean checkStoragePermission() {
        return true;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // Android 11 及以上
//            return Environment.isExternalStorageManager();
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // Android 6.0 到 Android 10
//            int readPermission = ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE);
//            int writePermission = ContextCompat.checkSelfPermission(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            return readPermission == PackageManager.PERMISSION_GRANTED &&
//                    writePermission == PackageManager.PERMISSION_GRANTED;
//        }
//        return true;
    }

    /**
     * 请求存储权限
     */
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 及以上，需要申请所有文件访问权限
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, REQUEST_MANAGE_STORAGE);
            }
        } else {
            // Android 10 及以下
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * 初始化应用
     */
    private void initApp() {
        // 初始化数据库
        DBUntil db = new DBUntil(this);

        // 直接跳转到音乐主界面
        Intent intent = new Intent(MainActivity.this, UserManageActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initApp();
            } else {
                Tools.Toast(this, "需要存储权限才能播放音乐");
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    initApp();
                } else {
                    Tools.Toast(this, "需要文件管理权限才能播放音乐");
                    finish();
                }
            }
        }
    }
}