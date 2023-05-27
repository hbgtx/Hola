package com.hbgtx.hola.managers;

import static com.hbgtx.hola.utils.ConstantUtils.DEFAULT_PORT;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_CONTENT_INFO;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_MESSAGE_EXTRA;
import static com.hbgtx.hola.utils.ConstantUtils.KEY_USER_ID;
import static com.hbgtx.hola.utils.ConstantUtils.MESSAGE_USER_ID_RECEIVED;
import static com.hbgtx.hola.utils.ConstantUtils.SERVER_IP_ADDRESS;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbgtx.hola.callbacks.LoginCallback;
import com.hbgtx.hola.model.EntityId;
import com.hbgtx.hola.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionManager {
    private static final String USER_ID_REQUEST = "request_user_id";
    private static ConnectionManager connectionManager;
    private static final AtomicBoolean keepRunning = new AtomicBoolean(true);
    private Socket socket;
    private PrintWriter out;
    private boolean userIdReceived = false;

    private ConnectionManager() {

    }

    public static ConnectionManager getInstance() {
        if (connectionManager != null) {
            return connectionManager;
        }
        return connectionManager = new ConnectionManager();
    }

    public void tryLogin(EntityId userId, LoginCallback loginCallback) {
        new Thread(() -> tryRegisteringUser(userId, loginCallback)).start();
    }

    private void tryRegisteringUser(EntityId userId, LoginCallback loginCallback) {
        keepRunning.set(true);
        setupTimeout();
        try {
            socket = new Socket(SERVER_IP_ADDRESS, DEFAULT_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendUserIdWithRetries(userId);
            String inputLine;
            int readResult;
            while (keepRunning.get() && (readResult = in.read()) != -1) {
                inputLine = (char) readResult + in.readLine();
                System.out.println("Message Received from server:" + inputLine);
                JsonObject messageJson = (JsonObject) JsonParser.parseString(inputLine);
                if (messageJson != null) {

                    if (messageJson.has("request")) {
                        if (messageJson.get("request").toString().equals(USER_ID_REQUEST)) {
                            sendUserId(userId);
                        }
                    } else {
                        try {
                            Message message = Message.getMessageFromString(inputLine);
                            if (message == null) {
                                continue;
                            }
                            JsonObject messageContent = (JsonObject) JsonParser.parseString(message.getMessageContent().getContent());
                            String info = messageContent.get(KEY_MESSAGE_CONTENT_INFO).getAsString();
                            if (info.equals(MESSAGE_USER_ID_RECEIVED)) {
                                String receivedUserId = messageContent.get(KEY_MESSAGE_EXTRA).getAsString();
                                if (receivedUserId.equals(userId.getId())) {
                                    userIdReceived = true;
                                    loginCallback.onSuccessfulLogin(new EntityId(receivedUserId));
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Exception while parsing message:" + inputLine);
                        }
                    }
                }
            }
            if (!keepRunning.get()) {
                loginCallback.onLoginFailure("Server Timeout while trying to login.");
            }
            System.out.println("End reached");
        } catch (IOException e) {
            System.out.println("IO exception while trying to login");
            loginCallback.onLoginFailure("Cannot connect to server.");
        }
    }

    private void setupTimeout() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(this::stopListening, 2000, TimeUnit.MILLISECONDS);
    }

    private void sendUserIdWithRetries(EntityId userId) {
        (new UserIdSender(userId)).start();
    }

    public void stopListening() {
        try {
            keepRunning.set(false);
            userIdReceived = false;
            if (socket != null) {
                socket.close();
            }
            System.out.println("Socket closed");
        } catch (IOException e) {
            System.out.println("Exception while closing the socket");
        }
    }

    private void sendUserId(EntityId userId) {
        if (out != null) {
            JsonObject userIdObject = new JsonObject();
            userIdObject.addProperty(KEY_USER_ID, userId.getId());
            out.println(userIdObject);
        }
    }

    private class UserIdSender extends Thread {
        private final EntityId userId;
        private int retries = 0;

        private UserIdSender(EntityId userId) {
            this.userId = userId;
        }

        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            while (!userIdReceived && retries++ < 3) {
                System.out.println("Retrieying " + retries);
                sendUserId(userId);
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    break;
                }
            }

        }
    }
}
