package com.hbgtx.hola.model;

import androidx.annotation.NonNull;

public class MessageContent {
    private final String content;

    public MessageContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @NonNull
    @Override
    public String toString() {
        return content != null ? content : "";
    }
}
