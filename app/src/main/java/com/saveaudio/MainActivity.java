package com.saveaudio;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.saveaudio.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.i(FileUtil.TAG, "save button click");
        initRequestPermission();
    }

    public void initRequestPermission() {
        if (FileUtil.checkPermissionWRITE_EXTERNAL_STORAGE(this)) {
            FileUtil.setPermissionWriteDenied(true);
            saveAudioInMemory(R.raw.test3);
        }
    }

    private void saveAudioInMemory(int resource) {
        if (FileUtil.isPermissionWriteDenied()) {
            File file = FileUtil.getNewFile(getApplicationContext(), getString(R.string.folder_name), ".mp3"); // создание директории и имени файла
            InputStream in = getResources().openRawResource(resource);
            if (file != null) {
                if (resource == 0) {
                    throw new IllegalArgumentException("resource should not be null");
                }
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    int length = in.available();
                    Log.i(FileUtil.TAG, "file length = " + length);
                    byte[] buff = new byte[length];
                    int read = 0;
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                    out.flush();
                    in.close();
                    out.close();
                    Toast.makeText(this, getString(R.string.saved_in) + file.getAbsolutePath(),
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, getString(R.string.the_file_is_null), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FileUtil.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FileUtil.setPermissionWriteDenied(true);
                    Log.i(FileUtil.TAG, getString(R.string.permission_allowed));
                    initRequestPermission();
                } else {
                    FileUtil.setPermissionWriteDenied(false);
                    Log.i(FileUtil.TAG, getString(R.string.permission_denied));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
