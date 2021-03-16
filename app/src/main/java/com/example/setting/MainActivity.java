package com.example.setting;

import android.Manifest;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.setting.appaddition.AppAdditionActivity;
import com.example.setting.data.DatabaseContract;
import com.example.setting.permission.MyPermissionClass;
import com.example.setting.recyclerview.Adapter;
import com.example.setting.recyclerview.Data;
import com.example.setting.recyclerview.PersonalRecyclerView;
import com.example.setting.service.MainService;
import com.example.setting.wifi.AddBottomSheet;
import com.example.setting.wifi.WifiData;
import com.example.setting.wifilist.ListBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MyPermissionClass {

    private Toolbar toolbar;
    private FloatingActionButton actionButton;
    private PersonalRecyclerView recyclerView;
    private View emptyScreen;
    private List<Data> appList;
    private PackageManager packageManager;
    Adapter adapter;
    private AddBottomSheet bottomSheet;
    private WifiManager wifiManager;
    AlarmManager alarmManager;
    private boolean isFromMain = false;
    private boolean isAlreadyRunning;
    private CoordinatorLayout coordinatorLayout;
    private Intent serviceIntent;
    private NotificationManagerCompat notificationManager;

    private static final int REQUEST_CODE_SERVICE = 3;
    private static final int REQUEST_CODE_MAIN = 10;
    private static final int REQUEST_CODE_WIFI = 7;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Manifest.permission.CHANGE_WIFI_STATE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        setSupportActionBar(toolbar);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppAdditionActivity.class);
                startActivityForResult(intent, 100);
            }
        });
        recyclerView.setHiddenViews(emptyScreen);
        recyclerView.setShownViews(recyclerView);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        super.requestPermission(permissions, REQUEST_CODE_MAIN);

        loadApp();

        setRecycler();

        setSwap();

    }

    private void setSwap() {
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.END);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (viewHolder instanceof Adapter.PersonalViewHolder) {
                    final int position = (int) ((Adapter.PersonalViewHolder) viewHolder).itemView.getTag();
                    final String deletedItemName = appList.get(position).getName();
                    final String deletedPackageName = appList.get(position).getPackageName();
                    final Drawable deletedItemIcon = appList.get(position).getIcon();
                    int result = getContentResolver().delete(DatabaseContract.AppDataEntry.CONTENT, DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME + "=?", new String[]{appList.get(position).getPackageName()});
                    if (result > 0) {
                        appList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, appList.size());
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, deletedItemName + "  Listeden Kaldırıldı", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Geri Al", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ContentValues values = new ContentValues();
                                values.put(DatabaseContract.AppDataEntry.COLUMN_NAME, deletedItemName);
                                values.put(DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME, deletedPackageName);
                                Uri insertResult = getContentResolver().insert(DatabaseContract.AppDataEntry.CONTENT, values);
                                if (ContentUris.parseId(insertResult) != -1) {
                                    appList.add(position, new Data(deletedItemIcon, deletedItemName, deletedPackageName));
                                    adapter.notifyItemInserted(position);
                                    adapter.notifyItemRangeChanged(position, appList.size());
                                }
                            }
                        });
                        snackbar.show();
                    }


                }
            }
        });

        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void setRecycler() {
        adapter = new Adapter(appList, this, getLayoutInflater());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void loadApp() {
        String appName, appPackageName;
        Drawable appIcon;
        ApplicationInfo appInfo;
        appList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(DatabaseContract.AppDataEntry.CONTENT, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                appName = cursor.getString(cursor.getColumnIndex(DatabaseContract.AppDataEntry.COLUMN_NAME));
                appPackageName = cursor.getString(cursor.getColumnIndex(DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME));

                try {
                    appInfo = packageManager.getApplicationInfo(appPackageName, PackageManager.GET_META_DATA);
                    appIcon = packageManager.getApplicationIcon(appInfo);
                    appList.add(new Data(appIcon, appName, appPackageName));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initialise() {
        toolbar = findViewById(R.id.toolbar);
        actionButton = findViewById(R.id.action_button);
        recyclerView = findViewById(R.id.recylerView);
        emptyScreen = findViewById(R.id.emptyScreen);
        packageManager = getPackageManager();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_add_wifi) {
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                super.requestPermission(permissions, REQUEST_CODE_WIFI);
            } else {
                bottomSheet = new AddBottomSheet();
                bottomSheet.wifiDataList = new ArrayList<>();
                wifiManager.startScan();
                isFromMain = true;
            }
            return true;

        } else if (item.getItemId() == R.id.action_start) {
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                super.requestPermission(permissions, REQUEST_CODE_SERVICE);
            } else {
                serviceIntent = new Intent(this, MainService.class);
                serviceIntent.putExtra("message", "Uygulama Başlatıldı Start");
                startService(serviceIntent);
            }
            return true;

        } else if (item.getItemId() == R.id.action_stop) {
            serviceIntent = new Intent(this, MainService.class);
            stopService(serviceIntent);
            return true;

        } else if (item.getItemId() == R.id.wifi_list) {
            ListBottomSheet listBottomSheet = new ListBottomSheet();
            Cursor cursor = getContentResolver().query(DatabaseContract.WifiNameEntry.WCONTENT, null, null, null, null);
            listBottomSheet.wifiDataList = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    boolean isAlways = cursor.getInt(cursor.getColumnIndex(DatabaseContract.WifiNameEntry.COLUMN_ISALWAYS)) == 1;
                    listBottomSheet.wifiDataList.add(new WifiData(cursor.getString(cursor.getColumnIndex(DatabaseContract.WifiNameEntry.COLUMN_NAME)), !isAlways, isAlways));
                }
            }
            listBottomSheet.show(getSupportFragmentManager(), "ListWifi");
            return true;
        } else
            return super.onOptionsItemSelected(item);

    }

    @Override
    public void permissionGiven(int requestCode) {
        if (requestCode == REQUEST_CODE_SERVICE) {
            serviceIntent = new Intent(this, MainService.class);
            serviceIntent.putExtra("message", "Uygulama Başlatıldı Permission");
            startService(serviceIntent);

        } else if (requestCode == REQUEST_CODE_WIFI) {
            bottomSheet = new AddBottomSheet();
            bottomSheet.wifiDataList = new ArrayList<>();
            registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
            isFromMain = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) && isFromMain) {
                List<ScanResult> mScanResults = wifiManager.getScanResults();

                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                for (ScanResult temp : mScanResults) {
                    bottomSheet.wifiDataList.add(new WifiData(temp.SSID, false, false));
                }
                bottomSheet.show(getSupportFragmentManager(), "AddWifi");
                isFromMain = false;
            }

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mWifiScanReceiver);
        serviceIntent = new Intent(this, MainService.class);
        isAlreadyRunning = stopService(serviceIntent);
        if (isAlreadyRunning) {
            serviceIntent = new Intent(this, MainService.class);
            serviceIntent.putExtra("message", "dontshow");
            startService(serviceIntent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            appList.clear();
            loadApp();
            adapter = new Adapter(appList, this, getLayoutInflater());
            recyclerView.setAdapter(adapter);
        }
    }
}