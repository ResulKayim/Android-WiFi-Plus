package com.example.setting.wifi;

public class WifiData {
    private String wifiName;
    private boolean isForApps;
    private boolean isAlways;

    public WifiData(String wifiName, boolean isForApps, boolean isAlways) {
        this.wifiName = wifiName;
        this.isForApps = isForApps;
        this.isAlways = isAlways;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public boolean isForApps() {
        return isForApps;
    }

    public void setForApps(boolean checked) {
        isForApps = checked;
    }

    public boolean isAlways() {
        return isAlways;
    }

    public void setAlways(boolean always) {
        isAlways = always;
    }
}
