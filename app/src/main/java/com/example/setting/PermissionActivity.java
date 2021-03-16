package com.example.setting;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PermissionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Switch permissionSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        toolbar = findViewById(R.id.permission_toolbar);
        permissionSwitch = findViewById(R.id.permission_switch);
        setSupportActionBar(toolbar);

        if (isPermissionGiven()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            permissionSwitch.setChecked(false);
        }

        permissionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 100);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && isPermissionGiven()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isPermissionGiven()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            permissionSwitch.setChecked(false);
        }
    }

    private boolean isPermissionGiven() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

}
