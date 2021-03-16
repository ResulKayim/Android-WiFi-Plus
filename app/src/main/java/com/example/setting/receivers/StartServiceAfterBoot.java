package com.example.setting.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.example.setting.service.MainService;

public class StartServiceAfterBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MainService.class);
        ContextCompat.startForegroundService(context, intent1);
    }
}
