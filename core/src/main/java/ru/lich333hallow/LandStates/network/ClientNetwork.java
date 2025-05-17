package ru.lich333hallow.LandStates.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

public class ClientNetwork {
    private Socket socket;

    public void connect(String host, int port){
        try {
            SocketHints socketHints = new SocketHints();
            socketHints.connectTimeout = 5000;

            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host, port, socketHints);
        } catch (Exception e){
            Gdx.app.error("Network", "Connection error" + e);
        }
    }
}

