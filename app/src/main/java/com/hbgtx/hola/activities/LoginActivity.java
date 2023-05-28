package com.hbgtx.hola.activities;

import static com.hbgtx.hola.utils.Util.isValidUserId;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hbgtx.hola.R;
import com.hbgtx.hola.callbacks.LoginCallback;
import com.hbgtx.hola.listeners.MessageListener;
import com.hbgtx.hola.managers.ConnectionManager;
import com.hbgtx.hola.model.EntityId;
import com.hbgtx.hola.utils.SharedPrefUtil;

public class LoginActivity extends AppCompatActivity {
    private EditText userIdText;
    private Button loginButton;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ConnectionManager.destroyInstance();
        MessageListener.destroyInstance();
        connectionManager = ConnectionManager.getInstance();
        userIdText = (EditText) findViewById(R.id.edit_text_user_id);
        loginButton = (Button) findViewById(R.id.login_btn);

        loginButton.setOnClickListener(v -> {
            String userId = userIdText.getText().toString();
            if (!isValidUserId(userId)) {
                Toast.makeText(this, "User id must be of length 3", Toast.LENGTH_LONG).show();
                return;
            }
            loginButton.setEnabled(false);
            connectionManager.tryLogin(new EntityId(userId), new LoginCallback() {
                @Override
                public void onSuccessfulLogin(EntityId userId) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Logged in successfully for " + userId.getId(), Toast.LENGTH_SHORT).show();
                        (new SharedPrefUtil(LoginActivity.this)).setUserId(userId);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        loginButton.setEnabled(true);
                        finish();
                    });
                }

                @Override
                public void onLoginFailure(String failureMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
                        loginButton.setEnabled(true);
                    });
                }
            });
        });
    }
}