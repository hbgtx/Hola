package com.hbgtx.hola.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.hbgtx.hola.R;

public class SharedPrefUtil {

    private final SharedPreferences sharedPreferences;
    private final Context context;

    public SharedPrefUtil(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public boolean isBiometricEnabled() {
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.biometric_key), false);
    }
}
