package ru.lich333hallow.LandStates.network;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSockets;

import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.data.WebSocketException;
import com.github.czyzby.websocket.data.WebSocketState;

public class WebSocketClient {
    private WebSocket webSocket;
    private final String serverUrl;
    private final Array<WebSocketListener> listeners = new Array<>();
    private boolean autoReconnect = true;
    private final int reconnectDelay = 5000;
    private int currentReconnectAttempts = 0;
    private boolean manuallyClosed = false;

    public WebSocketClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void connect() {
        if (isConnected() || webSocket != null) return;

        manuallyClosed = false;
        currentReconnectAttempts = 0;

        try {
            webSocket = WebSockets.newSocket(serverUrl);

            webSocket.addListener(new WebSocketAdapter() {
                @Override
                public boolean onOpen(WebSocket webSocket) {
                    currentReconnectAttempts = 0;
                    notifyConnected();
                    return FULLY_HANDLED;
                }

                @Override
                public boolean onClose(WebSocket webSocket, int code, String reason) {
                    notifyDisconnected();
                    attemptReconnect();
                    return FULLY_HANDLED;
                }

                @Override
                public boolean onMessage(WebSocket webSocket, String packet) {
                    notifyMessageReceived(packet);
                    return FULLY_HANDLED;
                }

                @Override
                public boolean onError(WebSocket webSocket, Throwable error) {
                    notifyError(error);
                    attemptReconnect();
                    return FULLY_HANDLED;
                }
            });

            webSocket.connect();
        } catch (WebSocketException e) {
            notifyError(e);
            attemptReconnect();
        }
    }

    private void attemptReconnect() {
        int maxReconnectAttempts = 5;
        if (manuallyClosed || !autoReconnect || currentReconnectAttempts >= maxReconnectAttempts) {
            return;
        }

        currentReconnectAttempts++;
        Gdx.app.postRunnable(() -> {
            try {
                Thread.sleep(reconnectDelay);
                connect();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void sendMessage(String message) {
        if (isConnected()) {
            try {
                webSocket.send(message);
            } catch (WebSocketException e) {
                notifyError(e);
            }
        }
    }

    public boolean isConnected() {
        return webSocket != null && webSocket.getState() == WebSocketState.OPEN;
    }

    public void disconnect() {
        manuallyClosed = true;
        if (webSocket != null) {
            try {
                webSocket.close(WebSocketCloseCode.NORMAL);
            } catch (WebSocketException e) {
                notifyError(e);
            }
        }
    }

    public void addListener(WebSocketListener listener) {
        if (!listeners.contains(listener, true)) {
            listeners.add(listener);
        }
    }

    public void removeListener(WebSocketListener listener) {
        listeners.removeValue(listener, true);
    }

    private void notifyConnected() {
        for (WebSocketListener listener : listeners) {
            listener.connected();
        }
    }

    private void notifyDisconnected() {
        for (WebSocketListener listener : listeners) {
            listener.disconnected();
        }
    }

    private void notifyMessageReceived(String message) {
        for (WebSocketListener listener : listeners) {
            listener.messageReceived(message);
        }
    }

    private void notifyError(Throwable error) {
        for (WebSocketListener listener : listeners) {
            listener.errorOccurred(error);
        }
    }

    public void sendJoinLobbyRequest(String lobbyId, String playerId, String playerName) {
        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("type", new JsonValue("JOIN"));
        json.addChild("lobbyId", new JsonValue(lobbyId));
        json.addChild("playerId", new JsonValue(playerId));
        json.addChild("playerName", new JsonValue(playerName));

        sendMessage(json.toJson(JsonWriter.OutputType.json));
    }
}

