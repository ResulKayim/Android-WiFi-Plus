package com.example.setting.wifi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.setting.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class AddBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    public List<WifiData> wifiDataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.button_add_wifi_sheet, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSheet);



        WifiSheetAdapter sheetAdapter = new WifiSheetAdapter(getContext(), (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE), wifiDataList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setAdapter(sheetAdapter);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

}