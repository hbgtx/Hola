package com.hbgtx.hola.handlers;

import com.hbgtx.hola.model.Message;

public abstract class BaseMessageHandler {
    abstract boolean handleMessage(Message message);
}
