package com.example.chatapp.Classes;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestHandler{
    public final String hostname;
    public final String port;
    private final String baseUrl;
    private final OkHttpClient client = new OkHttpClient();

    public RequestHandler(String hostname, String port){
        this.hostname = hostname;
        this.port = port;
        this.baseUrl = hostname + ":" + port + "/";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private Request qBuild(String url){
        return new Request.Builder()
                .url(url)
                .build();
    }

    public JSONArray responseToJSONArray(Response response){
        JSONArray jsonArray = null;
        try{
            assert response.body() != null;
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("test");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public Response get(String resource, String queryParam){
        String url = this.baseUrl + resource + "?" + queryParam;

        Request request = qBuild(url);
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public Response post(String resource, Map<String, String> queryParams) {
        String url = this.baseUrl + resource + "/";
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
            try {
                jsonObject.put(queryParam.getKey(), queryParam.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String json = jsonObject.toString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response;
            } else {
                Log.e("RequestHandler POST", "Tried sending request to "+url);
                assert response.body() != null;
                Log.e("RequestHandler POST","Couldn't send message, " + response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}