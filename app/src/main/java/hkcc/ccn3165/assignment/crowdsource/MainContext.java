/*
 * WiFiAnalyzer
 * Copyright (C) 2019 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package hkcc.ccn3165.assignment.crowdsource;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.LayoutInflater;

import com.vrem.wifianalyzer.settings.Repository;
import com.vrem.wifianalyzer.settings.Settings;
/* import com.vrem.wifianalyzer.vendor.model.VendorService;
import com.vrem.wifianalyzer.vendor.model.VendorServiceFactory;
import com.vrem.wifianalyzer.wifi.filter.adapter.FilterAdapter;
import com.vrem.wifianalyzer.wifi.scanner.ScannerService;
import com.vrem.wifianalyzer.wifi.scanner.ScannerServiceFactory; */

public enum MainContext {
    INSTANCE;

    private Settings settings;
    private MainActivity mainActivity;
    private WifiManager wifiManager;
    private GPSService mGPSService;
    private StdDBHelper dbHelper;
    // private ScannerService scannerService;
    // private VendorService vendorService;
    private Configuration configuration;
    // private FilterAdapter filterAdapter;
    private int screenWidth;

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public Settings getSettings() {
        return settings;
    }

    private void setSettings(Settings settings) {
        this.settings = settings;
    }

    public GPSService getGPSService() {
        return mGPSService;
    }

    private void setGPSService() {
        mGPSService = new GPSService(getMainActivity(), getScreenWidth());
    }

    public StdDBHelper getDBHelper() {
        return dbHelper;
    }

    private void setDBHelper() {
        dbHelper = new StdDBHelper(getContext());
    }
    /* public VendorService getVendorService() {
        return vendorService;
    }

    void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public ScannerService getScannerService() {
        return scannerService;
    }

    void setScannerService(ScannerService scannerService) {
        this.scannerService = scannerService;
    } */

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    private void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public Context getContext() {
        return mainActivity.getApplicationContext();
    }

    public Resources getResources() {
        return getContext().getResources();
    }

    public LayoutInflater getLayoutInflater() {
        return mainActivity.getLayoutInflater();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    private void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }


    public int getScreenWidth() {
        return screenWidth;
    }

    private void setScreenWidth() {
        // https://alvinalexander.com/android/how-to-determine-android-screen-size-dimensions-orientation
        Display display = mainActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    /* public FilterAdapter getFilterAdapter() {
        return filterAdapter;
    }

    void setFilterAdapter(FilterAdapter filterAdapter) {
        this.filterAdapter = filterAdapter;
    } */

    public void initialize(@NonNull MainActivity mainActivity) {
        Context applicationContext = mainActivity.getApplicationContext();
        wifiManager = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
        Handler handler = new Handler();
        Settings currentSettings = new Settings(new Repository(applicationContext));
        Configuration currentConfiguration = new Configuration();

        setMainActivity(mainActivity);
        setScreenWidth();
        setGPSService();
        setDBHelper();
        setConfiguration(currentConfiguration);
        setSettings(currentSettings);
        /* setVendorService(VendorServiceFactory.makeVendorService(mainActivity.getResources()));
        setScannerService(ScannerServiceFactory.makeScannerService(wifiManager, handler, currentSettings));
        setFilterAdapter(new FilterAdapter(currentSettings)); */
    }

}
