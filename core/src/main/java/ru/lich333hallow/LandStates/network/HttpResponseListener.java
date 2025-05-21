package ru.lich333hallow.LandStates.network;

public interface HttpResponseListener {
    void onSuccess(int statusCode, String response);
    void onError(int statusCode, String error);
    void onFailure(Throwable t);
}
