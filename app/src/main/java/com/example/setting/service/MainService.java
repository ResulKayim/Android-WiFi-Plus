package com.example.setting.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.setting.R;
import com.example.setting.data.DatabaseContract;
import com.example.setting.receivers.NotificationReceiver;
import com.example.setting.receivers.StopServiceReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class MainService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private List<String> wifiName;
    private List<String> alwaysWifiName;
    private List<String> packageName;
    private WifiManager wifiManager;
    private NotificationManagerCompat notificationManager;
    private boolean alwaysPermission;
    private String lastApp = "", currentApp = "";
    private String cancelledApp = "";
    private boolean isFromWifi = false;
    private boolean isFromAlwaysWifi = false;

    public static final String CHANNEL_1_ID = "channel1";
    private NotificationChannel channel;

    private boolean isReceiversRegistered = false;

    private Timer wifiTimer, alwaysWifiTimer;

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(alwaysBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiStateChangeListener, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        isReceiversRegistered = true;
        alwaysPermission = true;

        wifiName = new ArrayList<>();
        packageName = new ArrayList<>();
        alwaysWifiName = new ArrayList<>();

        Cursor cursor = getContentResolver().query(DatabaseContract.AppDataEntry.CONTENT, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                packageName.add(cursor.getString(cursor.getColumnIndex(DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME)));
            }
        }

        cursor = getContentResolver().query(DatabaseContract.WifiNameEntry.WCONTENT, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(DatabaseContract.WifiNameEntry.COLUMN_NAME);
            int isAlwaysIndex = cursor.getColumnIndex(DatabaseContract.WifiNameEntry.COLUMN_ISALWAYS);
            while (cursor.moveToNext()) {
                if (cursor.getInt(isAlwaysIndex) == 1) {
                    alwaysWifiName.add(cursor.getString(nameIndex));
                } else {
                    wifiName.add(cursor.getString(nameIndex));
                }
            }
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        notificationManager = NotificationManagerCompat.from(getApplicationContext());

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Wifi Setting App");

            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showStartedNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent actionIntent = new Intent(getApplicationContext(), StopServiceReceiver.class);
            PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 2, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action = new NotificationCompat.Action(R.drawable.ic_wifi_black_24dp, "Durdur", stopPendingIntent);


            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_wifi_black_24dp)
                    .setContentTitle("Wifi Ayarlayıcı")
                    .setContentText("Bildirimi Gizlemek İçin Dokun")
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .addAction(action)
                    .build();

            startForeground(1, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_wifi_black_24dp)
                    .setContentTitle("Wifi Ayarlayıcı")
                    .setContentText("Bildirimi Gizlemek İçin Dokun")
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .build();

            startForeground(1, new Notification());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        wifiTimer = new Timer();
        alwaysWifiTimer = new Timer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            showStartedNotification();
        else {
            showStartedNotification();
            startService(new Intent(this, FakeForeground.class));
        }

        wifiTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentApp = runTask(getApplicationContext());
                Log.e("Tag", currentApp);
                if (!currentApp.equals(lastApp)) {
                    if (!currentApp.equals("") && currentApp != null && !cancelledApp.equals(currentApp)) {
                        alwaysPermission = true;
                    }
                    lastApp = currentApp;
                    if (!wifiManager.isWifiEnabled() && packageName.contains(currentApp)) {
                        wifiManager.startScan();
                        Log.e("Tag", "ISIN");
                        isFromWifi = true;
                    }
                }
            }
        }, 0, 1000);

        if (!alwaysWifiName.isEmpty()) {
            alwaysWifiTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (wifiManager.isWifiEnabled())
                        alwaysPermission = false;
                    if (!wifiManager.isWifiEnabled() && alwaysPermission) {
                        //alwaysPermission = false;
                        wifiManager.startScan();
                        isFromAlwaysWifi = true;
                    }
                }
            }, 0, 1000);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isReceiversRegistered) {
            unregisterReceiver(alwaysBroadcastReceiver);
            unregisterReceiver(mWifiScanReceiver);
            unregisterReceiver(wifiStateChangeListener);
            isReceiversRegistered = false;
        }
        wifiTimer.cancel();
        alwaysWifiTimer.cancel();

    }

    private BroadcastReceiver wifiStateChangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED || wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
                    cancelledApp = runTask(getApplicationContext());
                    alwaysPermission = false;
                }
            }
        }
    };

    private BroadcastReceiver alwaysBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && isFromAlwaysWifi && !wifiManager.isWifiEnabled()) {
                boolean isOpened = false;
                List<ScanResult> mScanResults = wifiManager.getScanResults();
                for (ScanResult temp : mScanResults) {
                    if (alwaysWifiName.contains(temp.SSID)) {
                        wifiManager.setWifiEnabled(true);
                        isOpened = true;
                        break;
                    }
                }
                if (!isOpened)
                    alwaysPermission = true;
                else {
                    alwaysPermission = false;
                }

                isFromAlwaysWifi = false;
            } else
                return;
        }
    };

    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && isFromWifi && !wifiManager.isWifiEnabled()) {
                List<ScanResult> mScanResults = wifiManager.getScanResults();
                for (ScanResult temp : mScanResults) {
                    if (wifiName.contains(temp.SSID)) {
                        wifiManager.setWifiEnabled(true);
                        break;
                    }
                }
                isFromWifi = false;
            } else
                return;
        }
    };

    private String runTask(Context context) {
        String packageName = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();

            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    packageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }

        } else {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
            packageName = appProcessInfos.get(0).processName;
        }
        return packageName;
    }

}
