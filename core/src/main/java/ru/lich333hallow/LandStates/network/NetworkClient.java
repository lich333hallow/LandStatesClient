package ru.lich333hallow.LandStates.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestHeader;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

public class NetworkClient {
    private static final String TAG = "HTTP_CLIENT";
    private static final int DEFAULT_TIMEOUT = 5000;

    public static void get(String url, HttpResponseListener listener) {
        sendRequest(Net.HttpMethods.GET, url, null, null, listener);
    }

    public static void post(String url, JsonValue jsonBody, HttpResponseListener listener) {
        sendRequest(Net.HttpMethods.POST, url, jsonBody, "application/json", listener);
    }

    public static void put(String url, JsonValue jsonBody, HttpResponseListener listener) {
        sendRequest(Net.HttpMethods.PUT, url, jsonBody, "application/json", listener);
    }

    public static void delete(String url, HttpResponseListener listener) {
        sendRequest(Net.HttpMethods.DELETE, url, null, null, listener);
    }

    public static void sendRequest(String method, String url, JsonValue jsonBody,
                                   String contentType, HttpResponseListener listener){
        Net.HttpRequest request = new Net.HttpRequest(method);
        request.setUrl(url);
        request.setTimeOut(DEFAULT_TIMEOUT);

        if (contentType != null) {
            request.setHeader(HttpRequestHeader.ContentType, contentType);
        }
        request.setHeader("Accept", "application/json");

        if (jsonBody != null) {
            request.setContent(jsonBody.toJson(JsonWriter.OutputType.json));
        }

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = (int) httpResponse.getStatus().getStatusCode();
                String response = httpResponse.getResultAsString();

                Gdx.app.postRunnable(() -> {
                    if (statusCode >= 200 && statusCode < 300) {
                        listener.onSuccess(statusCode, response);
                    } else {
                        listener.onError(statusCode, response);
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> listener.onFailure(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() ->
                    listener.onFailure(new Exception("Request cancelled")));
            }
        });
    }

    public static JsonValue parseJson(String jsonString) {
        try {
            return new JsonReader().parse(jsonString);
        } catch (Exception e) {
            Gdx.app.error(TAG, "JSON parse error: " + e.getMessage());
            return null;
        }
    }
}
