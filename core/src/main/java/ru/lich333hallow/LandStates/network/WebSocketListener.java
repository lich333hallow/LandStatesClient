package ru.lich333hallow.LandStates.network;

public interface WebSocketListener {
    void connected();
    void disconnected();
    void messageReceived(String message);
    void errorOccurred(Throwable error);
}
