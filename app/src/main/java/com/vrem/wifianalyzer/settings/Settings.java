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

package com.vrem.wifianalyzer.settings;

import android.support.annotation.NonNull;

import com.vrem.util.EnumUtils;

import java.util.Set;

import hkcc.ccn3165.assignment.crowdsource.R;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class Settings {
    static final int SCAN_INTERVAL_DEFAULT = 5;
    static final int SCAN_INTERVAL_MIN_P = 30;

    private final Repository repository;

    public Settings(@NonNull Repository repository) {
        this.repository = repository;
    }

    public void initializeDefaultValues() {
        repository.initializeDefaultValues();
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        repository.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public boolean isWiFiOffOnExit() {
        return repository.getBoolean(R.string.wifi_off_on_exit_key, repository.getResourceBoolean(R.bool.wifi_off_on_exit_default));
    }

    public boolean isGPSOffOnExit() {
        return repository.getBoolean(R.string.gps_off_on_exit_key, repository.getResourceBoolean(R.bool.gps_off_on_exit_default));
    }

    @NonNull
    private <T extends Enum> T find(@NonNull Class<T> enumType, int key, @NonNull T defaultValue) {
        int value = repository.getStringAsInteger(key, defaultValue.ordinal());
        return EnumUtils.find(enumType, value, defaultValue);
    }

    @NonNull
    private <T extends Enum> Set<T> findSet(@NonNull Class<T> enumType, int key, @NonNull T defaultValue) {
        Set<String> defaultValues = EnumUtils.ordinals(enumType);
        Set<String> values = repository.getStringSet(key, defaultValues);
        return EnumUtils.find(enumType, values, defaultValue);
    }

    private <T extends Enum> void saveSet(int key, @NonNull Set<T> values) {
        repository.saveStringSet(key, EnumUtils.find(values));
    }
}
