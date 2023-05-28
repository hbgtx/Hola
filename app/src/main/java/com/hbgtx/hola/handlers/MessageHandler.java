package com.hbgtx.hola.handlers;

import com.hbgtx.hola.model.Message;

public class MessageHandler extends BaseMessageHandler {

    @Override
    public boolean handleMessage(Message message) {
        switch (message.getMessageType()) {
            case CHAT_MESSAGE:
                return handleChatMessage(message);
            case ACK_MESSAGE:
                return handleAckMessage(message);
            case TYPING_MESSAGE:
                return handleTypingMessage(message);
            case INFO_MESSAGE:
                return handleInfoMessage(message);
            case REQUEST_MESSAGE:
                return handleRequestMessage(message);
        }
        return false;
    }

    private boolean handleChatMessage(Message message) {
        System.out.println("handle chat message:" + message);
        return true;
    }

    private boolean handleAckMessage(Message message) {
        System.out.println("handle ack message:" + message);
        return true;
    }

    private boolean handleTypingMessage(Message message) {
        System.out.println("handle typing message:" + message);
        return true;
    }

    private boolean handleInfoMessage(Message message) {
        System.out.println("handle info message:" + message);
        return true;
    }

    private boolean handleRequestMessage(Message message) {
        System.out.println("handle request message:" + message);
        return true;
    }

}
