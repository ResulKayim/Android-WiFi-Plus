package com.example.setting.recyclerview;

import android.graphics.drawable.Drawable;

public class Data {
    private Drawable icon;
    private String name;
    private String packageName;
    private boolean isChecked;

    public Data(Drawable icon, String name, String packageName) {
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
        isChecked = false;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "Data{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '}';
    }
}
