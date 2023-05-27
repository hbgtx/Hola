package com.hbgtx.hola.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hbgtx.hola.utils.ConstantUtils.*;

public enum MessageType {
    CHAT_MESSAGE(MESSAGE_TYPE_CHAT), ACK_MESSAGE(MESSAGE_TYPE_ACK), TYPING_MESSAGE(MESSAGE_TYPE_TYPING),
    INFO_MESSAGE(MESSAGE_TYPE_INFO), REQUEST_MESSAGE(MESSAGE_TYPE_REQUEST);

    private static final Map<String, MessageType> MESSAGE_TYPE_MAP;

    static {
        Map<String, MessageType> map = new ConcurrentHashMap<>();
        for (MessageType instance : MessageType.values()) {
            map.put(instance.getType().toLowerCase(), instance);
        }
        MESSAGE_TYPE_MAP = Collections.unmodifiableMap(map);
    }

    private final String type;

    MessageType(String type) {
        this.type = type;
    }

    public static MessageType get(String name) {
        return MESSAGE_TYPE_MAP.get(name.toLowerCase());
    }

    public String getType() {
        return type;
    }
}
