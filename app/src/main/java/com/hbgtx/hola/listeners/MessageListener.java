package com.hbgtx.hola.listeners;

import com.hbgtx.hola.callbacks.LoginCallback;
import com.hbgtx.hola.callbacks.MessageCallback;
import com.hbgtx.hola.handlers.MessageHandler;
import com.hbgtx.hola.managers.ConnectionManager;
import com.hbgtx.hola.model.EntityId;
import com.hbgtx.hola.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageListener extends Thread {
    private static MessageListener messageListener;
    private final MessageHandler messageHandler;
    private final ConnectionManager connectionManager;
    private final AtomicBoolean keepRunning = new AtomicBoolean(true);
    private final AtomicBoolean alreadyRunning = new AtomicBoolean(false);
    private final EntityId userId;
    private final List<MessageCallback> messageCallbacks;
    private Socket socket;
    private BufferedReader in;

    private MessageListener(EntityId userId) {
        this.messageHandler = new MessageHandler();
        this.userId = userId;
        this.connectionManager = ConnectionManager.getInstance();
        this.messageCallbacks = new ArrayList<>();
        messageCallbacks.add(message -> {
            messageHandler.handleMessage(Message.getAckForMessage(message));
            // handle the message
            messageHandler.handleMessage(message);
        });
    }

    public static MessageListener getInstance(EntityId userId) {
        if (messageListener == null) {
            return messageListener = new MessageListener(userId);
        }
        return messageListener;
    }

    public static void destroyInstance() {
        if (messageListener != null) {
            messageListener.interrupt();
        }
        messageListener = null;
    }

    @Override
    public void run() {
        if (alreadyRunning.get()) {
            return;
        }
        if (!connectionManager.isUserIdReceived()) {
            return;
        }
        socket = connectionManager.getSocket();
        connectionManager.tryLogin(userId, new LoginCallback() {
            @Override
            public void onSuccessfulLogin(EntityId userId) {
                alreadyRunning.set(true);
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    connectionManager.sendMessage(Message.getRequestChatMessage(userId));
                    listenForMessages();
                } catch (IOException e) {
                    System.out.println("IO Exception while listening to messages from server");
                    e.printStackTrace();
                } finally {
                    alreadyRunning.set(false);
                }
            }

            @Override
            public void onLoginFailure(String failureMessage) {
            }
        });


    }

    private void listenForMessages() throws IOException {
        System.out.println("listening messages from server");
        String inputLine;
        int readResult;
        while (keepRunning.get() && ((readResult = in.read()) != -1)) {
            inputLine = (char) readResult + in.readLine();
            System.out.println("Message received:" + inputLine);
            System.out.println("char received:" + (char) readResult);
            Message message = Message.getMessageFromString(inputLine);
            System.out.println("parsed message" + message);
            if (message != null) {
                onMessageReceived(message);
            }
        }
    }

    public void stopListening() {
        this.keepRunning.set(false);
        System.out.println("Stopped listening for messages");
    }

    public void registerCallback(MessageCallback messageCallback) {
        messageCallbacks.add(messageCallback);
    }

    private void onMessageReceived(Message message) {
        messageCallbacks.forEach(messageCallback -> messageCallback.onMessageReceived(message));
    }
}
