package com.hbgtx.hola.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hbgtx.hola.R;
import com.hbgtx.hola.authentication.BiometricAuthenticator;
import com.hbgtx.hola.utils.SharedPrefUtil;

public class MainActivity extends AppCompatActivity {

    private SharedPrefUtil sharedPrefUtil;
    private BiometricAuthenticator biometricAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        sharedPrefUtil = new SharedPrefUtil(this);
        biometricAuthenticator = new BiometricAuthenticator(this);
        findViewById(R.id.settings_button).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        if (sharedPrefUtil != null && sharedPrefUtil.isBiometricEnabled()) {
            biometricAuthenticator.authenticate();
        }
    }
}