package com.example.setting.appaddition;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.setting.R;
import com.example.setting.data.DatabaseContract;
import com.example.setting.recyclerview.Data;

import java.util.ArrayList;
import java.util.List;

public class AdditionAdapter extends RecyclerView.Adapter<AdditionAdapter.PersonalViewHolder> implements Filterable {

    private Context context;
    private LayoutInflater inflater;
    private List<Data> dataList;
    public List<Data> dataListFull = new ArrayList<>();
    private List<String> selectedApps = new ArrayList<>();
    private LoadMoreListener loadMoreListener;
    private ContentResolver contentResolver;

    public AdditionAdapter(Context context, LayoutInflater inflater, List<Data> dataList, LoadMoreListener loadMoreListener) {
        this.context = context;
        this.inflater = inflater;
        this.dataList = dataList;
        this.dataListFull.addAll(dataList);
        this.loadMoreListener = loadMoreListener;
        this.contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(DatabaseContract.AppDataEntry.CONTENT, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                selectedApps.add(cursor.getString(cursor.getColumnIndex(DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME)));
            }
        }
    }


    public void addItem(Data data) {
        dataList.add(data);
        dataListFull.add(data);
        notifyItemInserted(dataList.size());
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        loadMoreListener.loadMore();
    }

    @NonNull
    @Override
    public PersonalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.app_addition_item_layout, parent, false);
        return new PersonalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalViewHolder holder, int position) {
        holder.appName.setText(dataList.get(position).getName());
        holder.appPackageName.setText(dataList.get(position).getPackageName());
        holder.appIcon.setImageDrawable(dataList.get(position).getIcon());


        if (selectedApps.contains(holder.appPackageName.getText().toString())) {
            holder.checkBox.setChecked(true);
            dataList.get(position).setChecked(true);
            holder.container.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.checkBox.setChecked(false);
            holder.container.setBackgroundColor(Color.WHITE);
        }

        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.recycler_anim));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class PersonalViewHolder extends RecyclerView.ViewHolder {

        ImageView appIcon;
        TextView appName;
        TextView appPackageName;
        CheckBox checkBox;
        RelativeLayout container;

        public PersonalViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon1);
            appName = itemView.findViewById(R.id.app_name1);
            appPackageName = itemView.findViewById(R.id.app_package_name1);
            checkBox = itemView.findViewById(R.id.checkbox1);
            container = itemView.findViewById(R.id.item_relativeLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        container.setBackgroundColor(Color.WHITE);
                        checkBox.setChecked(false);
                        dataList.get(getAdapterPosition()).setChecked(false);
                    } else {
                        if (!selectedApps.contains(appPackageName.getText().toString())) {
                            container.setBackgroundColor(Color.LTGRAY);
                            checkBox.setChecked(true);
                            dataList.get(getAdapterPosition()).setChecked(true);
                        }
                    }
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!selectedApps.contains(appPackageName.getText().toString())) {
                        if (isChecked) {
                            container.setBackgroundColor(Color.LTGRAY);
                            dataList.get(getAdapterPosition()).setChecked(true);
                            checkBox.setChecked(true);
                            selectedApps.add(dataList.get(getAdapterPosition()).getPackageName());

                            ContentValues values = new ContentValues();
                            values.put(DatabaseContract.AppDataEntry.COLUMN_NAME, appName.getText().toString());
                            values.put(DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME, appPackageName.getText().toString());

                            contentResolver.insert(DatabaseContract.AppDataEntry.CONTENT, values);
                        } else {
                            container.setBackgroundColor(Color.WHITE);
                            checkBox.setChecked(false);
                            dataList.get(getAdapterPosition()).setChecked(false);
                            selectedApps.remove(dataList.get(getAdapterPosition()).getPackageName());

                            contentResolver.delete(DatabaseContract.AppDataEntry.CONTENT, DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME + "=?", new String[]{appPackageName.getText().toString()});
                        }
                    }else {
                        if (!isChecked){
                            container.setBackgroundColor(Color.WHITE);
                            checkBox.setChecked(false);
                            dataList.get(getAdapterPosition()).setChecked(false);
                            selectedApps.remove(dataList.get(getAdapterPosition()).getPackageName());

                            contentResolver.delete(DatabaseContract.AppDataEntry.CONTENT, DatabaseContract.AppDataEntry.COLUMN_PACKAGE_NAME + "=?", new String[]{appPackageName.getText().toString()});
                        }
                    }
                }
            });

        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Data> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Data item : dataListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Data> shown = (List<Data>) results.values;
            dataList.clear();
            dataList.addAll(shown);
            notifyDataSetChanged();
        }
    };

}

interface LoadMoreListener {
    void loadMore();
}
