package com.example.chatapp.Classes;

import android.util.Log;

import com.example.chatapp.Models.ChatHistory;
import com.example.chatapp.Models.ChatItem;
import com.example.chatapp.Models.UserManager;
import com.example.chatapp.Models.WebServiceMessage;
import com.example.chatapp.Models.WebServiceUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Response;

public class WebService {
    public RequestHandler handler;
    public WebServiceUser webServiceUser;
    private Context context;

    public WebService(String hostname, String port, UserManager manager, Context context) {
        this.context = context;
        this.handler = new RequestHandler(hostname, port);
        this.webServiceUser = this.getUserByFirebaseUID(manager.uid);
        if(webServiceUser == null){
            Boolean success = this.register(manager.username, manager.password, manager.uid);
            if (!success)
                throw new RuntimeException("Could not register new firebase user");
        }
        this.login(webServiceUser.username, webServiceUser.password);
    }

    public Boolean register(String username, String password, String uid){
        if (username.isEmpty() || password.isEmpty()){
            Log.e("ChatService|Register:", "Empty credentials");
            return false;
        }
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("username", username);
        queryParams.put("password", password);
        queryParams.put("firebase_uid", uid);
        Response response = handler.post("register", queryParams);

        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);
            String message = jsonObject.getString("message");
            return message.equals("Registration successful");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean login(String username, String password){
        if (username.isEmpty() || password.isEmpty()){
            Log.e("ChatService|Login:", "Empty credentials");
            return false;
        }
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("username", username);
        queryParams.put("password", password);
        Response response = handler.post("login", queryParams);

        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);
            String message = jsonObject.getString("message");
            return message.equals("Login successful");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean logout(){
        if (webServiceUser == null){
            return true; // already logged out
        }

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", String.valueOf(webServiceUser.id));
        Response response =  handler.post("logout", queryParams);

        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);
            String message = jsonObject.getString("message");
            return message.equals("Logout successful");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Response sendMessage(String toID, String content){
        if (content.isEmpty()){
            Log.e("ChatService|SendMsg:", "Empty message");
            return null;
        }
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fromID", String.valueOf(webServiceUser.id));
        queryParams.put("toID", toID);
        queryParams.put("content", content);
        return handler.post("send-message", queryParams);
    }

    public Response test(){
        return handler.get("", "");
    }

    public WebServiceUser getUserByFirebaseUID(String firebase_uid){
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("firebase_uid", firebase_uid);
        Response response =  handler.post("get-user-by-firebase-uid", queryParams);
        WebServiceUser user = new WebServiceUser();

        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);
            user.id = jsonObject.getInt("id");
            user.username = jsonObject.getString("name");
            user.password = jsonObject.getString("password");
            user.firebase_uid = jsonObject.getString("firebase_uid");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public ArrayList<WebServiceMessage> getReceivedMessages(){
        Response response = handler.get("received-messages", "_id="+ webServiceUser.id);
        ArrayList<WebServiceMessage> webServiceMessages = new ArrayList<>();

        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                WebServiceMessage webServiceMessage = new WebServiceMessage();
                webServiceMessage.id = Integer.parseInt(jsonObject.getString("id"));
                webServiceMessage.fromID = jsonObject.getInt("fromID");
                webServiceMessage.toID = jsonObject.getInt("toID");
                webServiceMessage.content = jsonObject.getString("content");
                String date = jsonObject.getString("date");
                webServiceMessage.date = date;
                webServiceMessages.add(webServiceMessage);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return webServiceMessages;
    }

    public ArrayList<ChatHistory> getChatHistory() {
        Response response = handler.get("received-messages-by-users", "_id=" + webServiceUser.id);
        ArrayList<ChatHistory> chatHistoryArrayList = new ArrayList<>();

        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject chatObject = jsonObject.getJSONObject(key);
                JSONArray messageArray = chatObject.getJSONArray("messages");
                ArrayList<WebServiceMessage> messages = new ArrayList<>();
                for (int i = 0; i < messageArray.length(); i++) {
                    JSONObject messageObject = messageArray.getJSONObject(i);
                    int id = messageObject.getInt("id");
                    int fromID = messageObject.getInt("fromID");
                    int toID = messageObject.getInt("toID");
                    String content = messageObject.getString("content");
                    String date = messageObject.getString("date");
                    WebServiceMessage message = new WebServiceMessage(id, fromID, toID, content, date);
                    messages.add(message);
                }
                String username = chatObject.getString("username");
                String firebaseUid = chatObject.getString("firebase_uid");
                String lastMessage = chatObject.getString("last_message");
                String lastMessageDate = chatObject.getString("last_message_date");
                String unseenMessages = chatObject.getString("unseen_messages");
                ChatItem chatInfo = new ChatItem(key, firebaseUid, "", username, lastMessage, lastMessageDate, unseenMessages);

                chatHistoryArrayList.add(new ChatHistory(messages, chatInfo));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return chatHistoryArrayList;
    }

        public WebServiceUser getUserByID(int id){
        Response response = handler.get("get-user-by-id", "_id="+ id);
        WebServiceUser user = new WebServiceUser();

        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);
            user.id = jsonObject.getInt("id");
            user.username = jsonObject.getString("name");
            user.password = jsonObject.getString("password");
            user.firebase_uid = jsonObject.getString("firebase_uid");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
