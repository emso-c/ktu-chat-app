package com.example.chatapp.Classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chatapp.Models.ChatHistory;
import com.example.chatapp.Models.ChatItem;
import com.example.chatapp.Models.FirebaseUserInstance;
import com.example.chatapp.Models.WebServiceMessage;
import com.example.chatapp.Models.WebServiceUser;
import com.example.chatapp.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.here.oksse.OkSse;
import com.here.oksse.ServerSentEvent;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class WebService {
    public RequestHandler handler;
    public WebServiceUser webServiceUser;
    private Context context;

    public WebService(String hostname, String port, FirebaseUserInstance manager, Context context) {
        this.context = context;
        this.handler = new RequestHandler(hostname, port);
        this.webServiceUser = this.getUserByFirebaseUID(manager.uid);
        if(webServiceUser == null){
            Boolean success = this.register(manager.username, manager.password, manager.uid);
            this.webServiceUser = this.getUserByFirebaseUID(manager.uid);
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

    public void setSeen(int id){
        handler.get("set-message-seen", "_id="+id);
    }

    public void setTyping(boolean isTyping){
        handler.get("update-typing", "_id="+webServiceUser.id+"&is_typing="+isTyping);
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

    public Response updateUser(String status, String username, String password, String photoUrl){
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", String.valueOf(webServiceUser.id));
        queryParams.put("status", status);
        queryParams.put("username", username);
        queryParams.put("password", password);
        queryParams.put("photo_url", photoUrl);
        return handler.post("update-user", queryParams);
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
            user.firebaseUid = jsonObject.getString("firebase_uid");
            user.status = jsonObject.getString("status");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
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

    public ArrayList<ChatHistory> getChatHistoryArray() {
        Response response = handler.get("chat-history", "_id=" + webServiceUser.id);
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
                    int seen = messageObject.optInt("seen", 0);
                    boolean isSeen = (seen == 1);
                    WebServiceMessage message = new WebServiceMessage(
                            id, fromID, toID, content, date, isSeen);
                    messages.add(message);
                }
                String username = chatObject.getString("username");
                String firebaseUid = chatObject.getString("firebase_uid");
                String lastMessage = chatObject.getString("last_message");
                String lastMessageDate = chatObject.getString("last_message_date");
                String unseenMessages = chatObject.getString("unseen_messages");
                String lastSeen = chatObject.getString("last_seen");
                ChatItem chatInfo = new ChatItem(key, firebaseUid, "", username,
                        lastMessage,lastMessageDate, unseenMessages, lastSeen);

                chatHistoryArrayList.add(new ChatHistory(messages, chatInfo));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return chatHistoryArrayList;
    }

    public ChatHistory getChatHistory(String target_id) {
        Response response = handler.get("chat-history-with-user", "_id=" + webServiceUser.id + "&_target_id=" + target_id);
        ChatItem chatItem = null;
        ArrayList<WebServiceMessage> messages = new ArrayList<>();


        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONObject chatObject = new JSONObject(jsonString);
            JSONArray messageArray = chatObject.getJSONArray("messages");
            for (int i = 0; i < messageArray.length(); i++) {
                JSONObject messageObject = messageArray.getJSONObject(i);
                int id = messageObject.getInt("id");
                int fromID = messageObject.getInt("fromID");
                int toID = messageObject.getInt("toID");
                String content = messageObject.getString("content");
                String date = messageObject.getString("date");
                int seen = messageObject.optInt("seen", 0);
                boolean isSeen = (seen == 1);
                WebServiceMessage message = new WebServiceMessage(id, fromID, toID, content, date, isSeen);
                messages.add(message);
            }

            String username = chatObject.getString("username");
            int id = chatObject.getInt("user_id");
            String firebaseUid = chatObject.getString("firebase_uid");
            String lastMessage = chatObject.getString("last_message");
            String lastMessageDate = chatObject.getString("last_message_date");
            String unseenMessages = chatObject.getString("unseen_messages");
            String lastSeen = chatObject.getString("last_seen");
            chatItem = new ChatItem(
                    Integer.toString(id), firebaseUid, "", username,
                    lastMessage, lastMessageDate, unseenMessages, lastSeen);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return new ChatHistory(messages, chatItem);
    }

    public WebServiceUser getUserByUsername(String username){
        Response response = handler.get("get-user-by-username", "username=" + username);
        WebServiceUser user = new WebServiceUser();

        try {
            assert response.body() != null;
            String jsonString = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonString);
            user.id = jsonObject.getInt("id");
            user.username = jsonObject.getString("name");
            user.password = jsonObject.getString("password");
            user.firebaseUid = jsonObject.getString("firebase_uid");
            user.lastSeen = jsonObject.getString("last_seen");
            user.photoUrl = jsonObject.getString("photo_url");
            user.isOnline = jsonObject.getString("is_online").equals("1");
            user.isTyping = jsonObject.getString("is_typing").equals("1");
            user.status = jsonObject.getString("status");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public void listen_messages(Context context){
        // source: https://www.youtube.com/watch?v=p0E3vNY1jtE
        String url = this.context.getString(R.string.hostname)+":"+context.getString(R.string.port)+"/message-stream?_id="+ webServiceUser.id;
        Request request = new Request.Builder().url(url).build();
        OkSse okSse = new OkSse();
        ServerSentEvent sse = okSse.newServerSentEvent(request, new ServerSentEvent.Listener() {
            @Override
            public void onOpen(ServerSentEvent sse, Response response) {
                Log.d("SSE", "onOpen");
            }

            @Override
            public void onMessage(ServerSentEvent sse, String id, String event, String message) {
                try {
                    if (event.equals("message")){
                        Log.d("SSE", "onMessage: " + message);
                        JSONObject jsonObject = new JSONObject(message);
                        int fromID = jsonObject.getInt("fromID");
                        int toID = jsonObject.getInt("toID");
                        String content = jsonObject.getString("content");
                        String date = jsonObject.getString("date");
                        int seen = jsonObject.optInt("seen", 0);
                        boolean isSeen = (seen == 1);
                        WebServiceMessage webServiceMessage = new WebServiceMessage(0, fromID, toID, content, date, isSeen);
                        Intent intent = new Intent("new-message");
                        intent.putExtra("message", webServiceMessage);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                    else if (event.equals("seen")){
                        Log.d("SSE", "onSeen: " + message);
                        Intent intent = new Intent("seen");
                        intent.putExtra("message", "seen");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onComment(ServerSentEvent sse, String comment) {
                Log.d("SSE", "onComment: " + comment);
            }

            @Override
            public boolean onRetryTime(ServerSentEvent sse, long milliseconds) {
                Log.d("SSE", "onRetryTime: " + milliseconds);
                return false;
            }

            @Override
            public boolean onRetryError(ServerSentEvent sse, Throwable throwable, Response response) {
                Log.d("SSE", "onRetryError: " + throwable.getMessage());
                return false;
            }

            @Override
            public void onClosed(ServerSentEvent sse) {
                Log.d("SSE", "onClosed");
            }

            @Override
            public Request onPreRetry(ServerSentEvent sse, Request originalRequest) {
                return null;
            }
        });
    }

    public void putProfilePicture(ImageView imageView){
        try{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            storageRef.child("profile_photos/"+webServiceUser.firebaseUid).getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(imageView);
            }).addOnFailureListener(exception -> {
                Log.e("ERROR PP DOWNLOAD INS", exception.toString());
            });
        } catch (Exception e){
            e.printStackTrace();
            Log.e("ERROR DOWNLOADING IMAGE", e.toString());
        }
    }

    private void getProfilePicture() {

    }
}
