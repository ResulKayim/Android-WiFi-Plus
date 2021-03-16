package com.example.setting.permission;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public abstract class MyPermissionClass extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void requestPermission(final String[] requested, final int requestCode) {
        int permissionControl = PackageManager.PERMISSION_GRANTED;
        boolean excuse = false;

        for (String per : requested) {
            permissionControl += ActivityCompat.checkSelfPermission(this, per);
            excuse = excuse || ActivityCompat.shouldShowRequestPermissionRationale(this, per);
        }

        if (permissionControl != PackageManager.PERMISSION_GRANTED) {

            if (excuse) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("İzni vermelisiniz");
                alertDialog.setMessage("Uygulamanın düzgün çalışması için bu izni vermelisiniz");
                alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MyPermissionClass.this, requested, requestCode);
                    }
                });

                alertDialog.show();
            } else {
                ActivityCompat.requestPermissions(this, requested, requestCode);
            }

        } else {
            permissionGiven(requestCode);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int requestResult = PackageManager.PERMISSION_GRANTED;

        for (int temp : grantResults) {
            requestResult += temp;
        }

        if (requestResult == PackageManager.PERMISSION_GRANTED) {

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("İzin Gerekli");
            builder.setMessage("Uygulamanın ulaşılabilir kablosuz ağları bulabilmesi için izin gerekli");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    /*intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    //intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package :" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);*/
                    startActivity(intent);
                }
            });

            builder.show();
        }

    }

    public abstract void permissionGiven(int requestCode);

}