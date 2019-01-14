package com.saveaudio.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {

    public static final String TAG = "FileUtil";
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    private static boolean permissionWriteDenied = false;

    private static File mediaStorageDir;

    private static String getFolderName(String name) {
        if(isPermissionWriteDenied()) {
            mediaStorageDir =
                    new File(Environment.getExternalStorageDirectory(),
                            name);
        }
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.i(TAG, "mkdirs");
                return "";
            }
        }
        return mediaStorageDir.getAbsolutePath();
    }

    private static boolean isSDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getNewFile(Context context, String folderName) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

        String timeStamp = simpleDateFormat.format(new Date());


        String path;
        if (isSDAvailable()) {
            Log.i(TAG, "SDAvailable");
            path = getFolderName(folderName);
        } else {
            Log.i(TAG, "SDNotAvailable");
            path = context.getFilesDir().getPath();
        }

        if (TextUtils.isEmpty(path)) {
            return null;
        }

        return new File(path, timeStamp + ".wav");
    }

    public static boolean checkPermissionWRITE_EXTERNAL_STORAGE(
            final Context context) {

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            }
        }
        else {
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }


    public static boolean isPermissionWriteDenied() {
        return permissionWriteDenied;
    }

    public static void setPermissionWriteDenied(boolean permissionWriteDenied) {
        FileUtil.permissionWriteDenied = permissionWriteDenied;
    }

}
