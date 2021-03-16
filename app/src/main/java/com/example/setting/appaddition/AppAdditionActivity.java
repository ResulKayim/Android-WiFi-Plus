package com.example.setting.appaddition;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.setting.R;
import com.example.setting.recyclerview.Data;

import java.util.ArrayList;
import java.util.List;

public class AppAdditionActivity extends AppCompatActivity implements LoadMoreListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<Data> dataList = new ArrayList<>();
    private AdditionAdapter additionAdapter;
    private PackageManager packageManager;
    private Handler handler;
    private List<ApplicationInfo> applicationInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_addition);
        initialise();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handler = new Handler();

        initialData();
        additionAdapter = new AdditionAdapter(getApplicationContext(), getLayoutInflater(), dataList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(additionAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_activity_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                additionAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    private void initialData() {
        String name, packageName;
        Drawable icon;
        applicationInfoList = packageManager.getInstalledApplications(128);
        for (ApplicationInfo temp : applicationInfoList) {
            if (packageManager.getLaunchIntentForPackage(temp.packageName) != null) {
                name = packageManager.getApplicationLabel(temp).toString();
                icon = packageManager.getApplicationIcon(temp);
                packageName = temp.packageName;
                dataList.add(new Data(icon, name, packageName));
                if (dataList.size() >= 10) {
                    break;
                }
            }
        }
    }

    private void initialise() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView1);
        packageManager = getPackageManager();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void loadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int counter = 0;
                String name, packageName;
                Drawable icon;
                for (ApplicationInfo temp : applicationInfoList) {
                    if (counter > 10) {
                        if (packageManager.getLaunchIntentForPackage(temp.packageName) != null) {
                            name = packageManager.getApplicationLabel(temp).toString();
                            icon = packageManager.getApplicationIcon(temp);
                            packageName = temp.packageName;
                            final Data data = new Data(icon, name, packageName);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dataList.add(data);
                                    additionAdapter.notifyItemInserted(dataList.size());
                                    additionAdapter.dataListFull.add(data);
                                }
                            }, 50);
                        }
                    }
                    counter++;
                }
            }
        }).start();
    }
}