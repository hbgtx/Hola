package com.hbgtx.hola.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.hbgtx.hola.R;
import com.hbgtx.hola.model.EntityId;

public class SharedPrefUtil {
    private static final String USER_ID_PROP = "user_id_prop";
    private final SharedPreferences sharedPreferences;
    private final Context context;

    public SharedPrefUtil(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public EntityId getUserId() {
        String storedUserId = sharedPreferences.getString(USER_ID_PROP, "");
        if (storedUserId.equals("")) {
            return null;
        }
        return new EntityId(storedUserId);
    }

    public void setUserId(EntityId userId) {
        sharedPreferences.edit().putString(USER_ID_PROP, userId.getId()).apply();
    }

    public boolean isBiometricEnabled() {
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.biometric_key), false);
    }
}
