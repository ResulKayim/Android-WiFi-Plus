package com.example.setting.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.setting.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.PersonalViewHolder> {

    private List<Data> dataList;
    private Context context;
    private LayoutInflater inflater;

    public Adapter(List<Data> dataList, Context context, LayoutInflater inflater) {
        this.dataList = dataList;
        this.context = context;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public PersonalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.app_item_layout, parent, false);
        return new PersonalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalViewHolder holder, int position) {
        holder.appName.setText(dataList.get(position).getName());
        holder.appIcon.setImageDrawable(dataList.get(position).getIcon());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class PersonalViewHolder extends RecyclerView.ViewHolder {

        ImageView appIcon;
        TextView appName;

        public PersonalViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
        }
    }
}
