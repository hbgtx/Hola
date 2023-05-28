package com.hbgtx.hola.activities;

import static com.hbgtx.hola.R.id.action_settings;
import static com.hbgtx.hola.utils.Util.isValidMessage;
import static com.hbgtx.hola.utils.Util.isValidUserId;
import static com.hbgtx.hola.utils.Util.shortToast;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbgtx.hola.R;
import com.hbgtx.hola.adapter.ChatListAdapter;
import com.hbgtx.hola.authentication.BiometricAuthenticator;
import com.hbgtx.hola.callbacks.ChatItemCallback;
import com.hbgtx.hola.callbacks.MessageCallback;
import com.hbgtx.hola.enums.MessageType;
import com.hbgtx.hola.listeners.MessageListener;
import com.hbgtx.hola.managers.ConnectionManager;
import com.hbgtx.hola.model.EntityId;
import com.hbgtx.hola.model.Message;
import com.hbgtx.hola.model.MessageContent;
import com.hbgtx.hola.utils.SharedPrefUtil;

public class MainActivity extends AppCompatActivity {

    private SharedPrefUtil sharedPrefUtil;
    private MessageListener messageListener;
    private ConnectionManager connectionManager;
    private EditText receiverEditText;
    private EditText messageEditText;
    private Button sendMessageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        receiverEditText = findViewById(R.id.edit_text_receiver_id);
        messageEditText = findViewById(R.id.edit_text_message);
        sendMessageBtn = findViewById(R.id.btn_send_message);
        setSupportActionBar(toolbar);
        sharedPrefUtil = new SharedPrefUtil(this);
        BiometricAuthenticator biometricAuthenticator = new BiometricAuthenticator(this);
        if (sharedPrefUtil.isBiometricEnabled()) {
            biometricAuthenticator.authenticate();
        }
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String receiverId = receiverEditText.getText().toString();
                String message = messageEditText.getText().toString();
                if (!isValidUserId(receiverId) || !isValidMessage(message)) {
                    return;
                }
                Message typingMessage = Message.getTypingMessage(sharedPrefUtil.getUserId(), new EntityId(receiverId));
                if (typingMessage != null) {
                    new Thread(() -> connectionManager.sendMessage(typingMessage)).start();
                }
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_chat_list);
        ChatListAdapter chatListAdapter = new ChatListAdapter(this, message -> receiverEditText.setText(message.getSenderId().getId()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(chatListAdapter);

        connectionManager = ConnectionManager.getInstance();
        messageListener = MessageListener.getInstance(sharedPrefUtil.getUserId());
        messageListener.start();
        messageListener.registerCallback(message -> runOnUiThread(() -> {
            if (message.getMessageType() == MessageType.CHAT_MESSAGE) {
                shortToast(MainActivity.this, message.getMessageContent().getContent());
                chatListAdapter.addMessage(message);
            }
        }));

        sendMessageBtn.setOnClickListener(v -> {
            String receiverId = receiverEditText.getText().toString();
            String message = messageEditText.getText().toString();
            if (!isValidUserId(receiverId)) {
                shortToast(MainActivity.this, "Receiver name must have atleast 3 characters");
                return;
            }

            if (!isValidMessage(message)) {
                shortToast(MainActivity.this, "Invalid Message");
                return;
            }
            new Thread(() -> {
                if (connectionManager.sendMessage(Message.getChatMessage(sharedPrefUtil.getUserId(), new EntityId(receiverId), new MessageContent(message)))) {
                    runOnUiThread(() -> {
                        receiverEditText.setText("");
                        messageEditText.setText("");
                    });


                }
            }).start();

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_acitivity_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTaskRoot()) {
            if (messageListener != null) {
                messageListener.stopListening();
            }
            connectionManager.stopListening();
        }
    }
}