package com.example.setting.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.setting.MainActivity;
import com.example.setting.service.MainService;

public class StopServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "onReceive: ");
        Intent serviceIntent = new Intent(context, MainService.class);
        context.stopService(serviceIntent);
    }
}
