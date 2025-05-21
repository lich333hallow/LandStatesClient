package ru.lich333hallow.LandStates.network;

public interface RunnableCallback {
    void onComplete();
    void onError(Exception e);
}
