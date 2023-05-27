package com.hbgtx.hola.model;

import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_CONTENT;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_ID;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_TYPE;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_RECEIVER;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_SENDER;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbgtx.hola.enums.MessageType;

public class Message {
    private static final Object mutex = new Object();
    private static int customMessageId = 1;
    private final MessageType messageType;
    private final EntityId messageId;
    private final EntityId senderId;
    private final EntityId receiverId;
    private final MessageContent messageContent;

    public Message(MessageType messageType, EntityId messageId, EntityId senderId, EntityId receiverId, MessageContent messageContent) {
        this.messageType = messageType;
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
    }

    public static Message getMessageFromString(String message) {
        try {
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(message);
            return new Message(
                    MessageType.get(jsonObject.get(KEY_MESSAGE_TYPE).getAsString()),
                    new EntityId(jsonObject.get(KEY_MESSAGE_ID).getAsString()),
                    new EntityId(jsonObject.get(KEY_SENDER).getAsString()),
                    new EntityId(jsonObject.get(KEY_RECEIVER).getAsString()),
                    new MessageContent(jsonObject.get(KEY_MESSAGE_CONTENT).getAsString()));
        } catch (Exception e) {
            System.out.println("Unable to parse message:" + message);
            return null;
        }
    }

    private static EntityId generateMessageId() {
        synchronized (mutex) {
            return new EntityId(String.valueOf(customMessageId++));
        }
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public EntityId getSenderId() {
        return senderId;
    }

    public EntityId getReceiverId() {
        return receiverId;
    }

    public MessageContent getMessageContent() {
        return messageContent;
    }

    @NonNull
    @Override
    public String toString() {
        JsonObject message = new JsonObject();
        message.addProperty(KEY_MESSAGE_TYPE, getMessageType().toString());
        message.addProperty(KEY_MESSAGE_ID, generateMessageId().toString());
        message.addProperty(KEY_SENDER, getSenderId().toString());
        message.addProperty(KEY_RECEIVER, getReceiverId().toString());
        message.addProperty(KEY_MESSAGE_CONTENT, getMessageContent().toString());
        return message.toString();
    }

}
