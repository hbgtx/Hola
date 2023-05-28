package com.hbgtx.hola.model;

import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_CONTENT;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_EXTRA;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_ID;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_TYPE;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_RECEIVER;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_SENDER;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_TIMESTAMP;
import static com.hbgtx.hola.utils.ConstantUtils.MESSAGE_REQUEST;
import static com.hbgtx.hola.utils.ConstantUtils.RESERVED_SERVER_ID;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbgtx.hola.enums.MessageType;

public class Message {

    private static final EntityId RESERVED_SERVER_ENTITY_ID = new EntityId(RESERVED_SERVER_ID);
    private static final Object mutex = new Object();
    private static int customMessageId = 1;
    private static long lastTypingMessageTimestamp = 0;
    private final EntityId messageId;
    private final MessageType messageType;
    private final EntityId senderId;
    private final EntityId receiverId;
    private final MessageContent messageContent;
    private final long timestamp;

    public Message(EntityId messageId, MessageType messageType, EntityId senderId, EntityId receiverId, MessageContent messageContent) {
        this(messageId, messageType, senderId, receiverId, messageContent, 0);
    }

    public Message(EntityId messageId, MessageType messageType, EntityId senderId, EntityId receiverId, MessageContent messageContent, long timestamp) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.timestamp = timestamp > 0 ? timestamp : System.currentTimeMillis();
    }

    public static Message getMessageFromString(String message) {
        try {
            JsonObject jsonObject = (JsonObject) JsonParser.parseString(message);
            return new Message(
                    new EntityId(jsonObject.get(KEY_MESSAGE_ID).getAsString()),
                    MessageType.get(jsonObject.get(KEY_MESSAGE_TYPE).getAsString()),
                    new EntityId(jsonObject.get(KEY_SENDER).getAsString()),
                    new EntityId(jsonObject.get(KEY_RECEIVER).getAsString()),
                    new MessageContent(jsonObject.get(KEY_MESSAGE_CONTENT).getAsString()),
                    jsonObject.get(KEY_TIMESTAMP).getAsLong());
        } catch (Exception e) {
            System.out.println("Unable to parse message:" + message);
            e.printStackTrace();
            return null;
        }
    }

    public static Message getAckForMessage(Message message) {
        return new Message(new EntityId(String.valueOf(generateMessageId())), MessageType.ACK_MESSAGE,
                message.getReceiverId(), RESERVED_SERVER_ENTITY_ID,
                getAckMessageContent(message));
    }

    public static Message getChatMessage(EntityId senderId, EntityId receiverId, MessageContent messageContent) {
        return new Message(generateMessageId(), MessageType.CHAT_MESSAGE, senderId, receiverId, messageContent);
    }

    public static Message getRequestChatMessage(EntityId userId) {
        return getChatMessage(userId, RESERVED_SERVER_ENTITY_ID, new MessageContent(MESSAGE_REQUEST));
    }

    public static Message getTypingMessage(EntityId senderId, EntityId receiverId) {
        if (!shouldSendTypingMessage()) {
            return null;
        }
        return new Message(new EntityId(String.valueOf(generateMessageId())), MessageType.TYPING_MESSAGE,
                senderId, receiverId,
                new MessageContent("typing"),
                lastTypingMessageTimestamp = System.currentTimeMillis());
    }

    private static MessageContent getAckMessageContent(Message message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(KEY_MESSAGE_EXTRA, message.getMessageId().getId());
        return new MessageContent(jsonObject.toString());
    }

    private static EntityId generateMessageId() {
        synchronized (mutex) {
            return new EntityId(String.valueOf(customMessageId++));
        }
    }

    public EntityId getMessageId() {
        return messageId;
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

    public long getTimestamp() {
        return timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        JsonObject message = new JsonObject();
        message.addProperty(KEY_MESSAGE_TYPE, getMessageType().getType());
        message.addProperty(KEY_MESSAGE_ID, generateMessageId().toString());
        message.addProperty(KEY_SENDER, getSenderId().toString());
        message.addProperty(KEY_RECEIVER, getReceiverId().toString());
        message.addProperty(KEY_MESSAGE_CONTENT, getMessageContent().toString());
        message.addProperty(KEY_TIMESTAMP, getTimestamp());
        return message.toString();
    }

    private static boolean shouldSendTypingMessage() {
        if (lastTypingMessageTimestamp == 0) {
            return true;
        }
        long currentTimestamp = System.currentTimeMillis();
        return currentTimestamp - lastTypingMessageTimestamp > 2000;
    }
}
