package ru.lich333hallow.LandStates.network;


import com.badlogic.gdx.Gdx;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;
import com.github.czyzby.websocket.WebSockets;

public class ClientNetwork {

    private WebSocket socket;
    private final String url = "ws://192.168.1.246:8080/ws";


    public void connect() {
        if (socket != null && socket.isOpen()) {
            socket.close();
        }

        socket = WebSockets.newSocket(url);

        socket.addListener(new WebSocketListener() {
            @Override
            public boolean onOpen(WebSocket webSocket) {
                Gdx.app.log("WS", "Connected!");
                return true;
            }

            @Override
            public boolean onClose(WebSocket webSocket, int closeCode, String reason) {
                Gdx.app.log("WS", "Disconnected: " + reason);
                return true;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, String packet) {
                Gdx.app.log("WS", "Received: " + packet);
                return true;
            }

            @Override
            public boolean onMessage(WebSocket webSocket, byte[] packet) {
                return false;
            }

            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
                Gdx.app.error("WS", "Error", error);
                return true;
            }
        });

        socket.connect();
    }

    public void sendMessage(String message) {
        if (socket != null && socket.isOpen()) {
            socket.send(message);
        } else {
            Gdx.app.error("WS", "Not connected!");
        }
    }

    public void disconnect() {
        if (socket != null) {
            socket.close();
        }
    }
}

