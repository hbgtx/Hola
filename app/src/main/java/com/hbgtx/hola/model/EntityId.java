package com.hbgtx.hola.model;

import androidx.annotation.NonNull;

public class EntityId {
    private final String id;

    public EntityId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return id != null? id : "";
    }
}
