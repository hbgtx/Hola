package com.hbgtx.hola.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbgtx.hola.R;
import com.hbgtx.hola.adapter.ChatListAdapter;
import com.hbgtx.hola.authentication.BiometricAuthenticator;
import com.hbgtx.hola.model.ChatItem;
import com.hbgtx.hola.utils.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

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
        if (sharedPrefUtil != null && sharedPrefUtil.isBiometricEnabled()) {
            biometricAuthenticator.authenticate();
        }

        setChatList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_acitivity_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setChatList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_chat_list);
        ChatListAdapter chatListAdapter = new ChatListAdapter(this, createChatList());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(chatListAdapter);
    }

    private List<ChatItem> createChatList() {
        List<ChatItem> chatItemList = new ArrayList<>();
        int n = 26;
        for (int i = 0; i < n; i++) {
            chatItemList.add(new ChatItem("person" + i));
        }
        return chatItemList;
    }
}