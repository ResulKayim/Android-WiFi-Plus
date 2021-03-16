package com.example.setting.wifi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.setting.R;
import com.example.setting.data.DatabaseContract;

import java.util.List;

public class WifiSheetAdapter extends RecyclerView.Adapter<WifiSheetAdapter.PersonalViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<WifiData> wifiDataList;

    public WifiSheetAdapter(Context context, LayoutInflater inflater, List<WifiData> wifiNames) {
        this.context = context;
        this.inflater = inflater;
        this.wifiDataList = wifiNames;
    }

    @NonNull
    @Override
    public PersonalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PersonalViewHolder(inflater.inflate(R.layout.botton_wifi_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalViewHolder holder, int position) {
        String name = wifiDataList.get(position).getWifiName();
        holder.nameText.setText(name);

        Cursor cursor = context.getContentResolver().query(DatabaseContract.WifiNameEntry.WCONTENT, null, DatabaseContract.WifiNameEntry.COLUMN_NAME + "=?", new String[]{name}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                boolean isAlways = cursor.getInt(cursor.getColumnIndex(DatabaseContract.WifiNameEntry.COLUMN_ISALWAYS)) == 1;
                if (isAlways)
                    holder.wifiStateSelection.setSelection(1);
                else if (!isAlways){
                    holder.wifiStateSelection.setSelection(2);
                }
                else
                    holder.wifiStateSelection.setSelection(0);
            }
        } else {
            if (wifiDataList.get(position).isForApps())
                holder.wifiStateSelection.setSelection(2);
            else if (wifiDataList.get(position).isAlways())
                holder.wifiStateSelection.setSelection(1);
            else
                holder.wifiStateSelection.setSelection(0);
        }
    }

    @Override
    public int getItemCount() {
        if (wifiDataList == null)
            return 0;
        return wifiDataList.size();
    }

    public class PersonalViewHolder extends RecyclerView.ViewHolder {


        TextView nameText;
        Spinner wifiStateSelection;

        public PersonalViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.wifi_name);
            wifiStateSelection = itemView.findViewById(R.id.wifiStateSelection);
            String[] states = {"Hiçbir zaman", "Her zaman", "Seçili uygulamalarda"};

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, states);
            wifiStateSelection.setAdapter(arrayAdapter);

            wifiStateSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 1) {

                        Cursor cursor = context.getContentResolver().query(DatabaseContract.WifiNameEntry.WCONTENT, null, DatabaseContract.WifiNameEntry.COLUMN_NAME + "=?", new String[]{nameText.getText().toString()}, null);
                        if (cursor != null) {
                            context.getContentResolver().delete(DatabaseContract.WifiNameEntry.WCONTENT, DatabaseContract.WifiNameEntry.COLUMN_NAME + "=?", new String[]{nameText.getText().toString()});
                        }
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContract.WifiNameEntry.COLUMN_NAME, nameText.getText().toString());
                        values.put(DatabaseContract.WifiNameEntry.COLUMN_ISALWAYS, 1);
                        context.getContentResolver().insert(DatabaseContract.WifiNameEntry.WCONTENT, values);
                        wifiDataList.get(getAdapterPosition()).setAlways(true);
                        wifiDataList.get(getAdapterPosition()).setForApps(false);
                    } else if (position == 2) {
                        Cursor cursor = context.getContentResolver().query(DatabaseContract.WifiNameEntry.WCONTENT, null, DatabaseContract.WifiNameEntry.COLUMN_NAME + "=?", new String[]{nameText.getText().toString()}, null);
                        if (cursor != null) {
                            context.getContentResolver().delete(DatabaseContract.WifiNameEntry.WCONTENT, DatabaseContract.WifiNameEntry.COLUMN_NAME + "=?", new String[]{nameText.getText().toString()});
                        }
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContract.WifiNameEntry.COLUMN_NAME, nameText.getText().toString());
                        values.put(DatabaseContract.WifiNameEntry.COLUMN_ISALWAYS, 0);
                        context.getContentResolver().insert(DatabaseContract.WifiNameEntry.WCONTENT, values);
                        wifiDataList.get(getAdapterPosition()).setAlways(false);
                        wifiDataList.get(getAdapterPosition()).setForApps(true);
                    } else {
                        context.getContentResolver().delete(DatabaseContract.WifiNameEntry.WCONTENT, DatabaseContract.WifiNameEntry.COLUMN_NAME + "=?", new String[]{nameText.getText().toString()});
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
    }
}
