package com.hbgtx.hola.callbacks;

import com.hbgtx.hola.model.Message;

public interface MessageCallback {
    void onMessageReceived(Message message);
}
