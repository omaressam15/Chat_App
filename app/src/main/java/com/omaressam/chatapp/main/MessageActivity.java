package com.omaressam.chatapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.omaressam.chatapp.Models.Chat;
import com.omaressam.chatapp.Models.User;
import com.omaressam.chatapp.Notifications.Data;
import com.omaressam.chatapp.Notifications.Sender;
import com.omaressam.chatapp.Notifications.Token;
import com.omaressam.chatapp.R;
import com.omaressam.chatapp.RecyclerView.MessageAdapter;
import com.omaressam.chatapp.Utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView profile_pic;
    TextView userName;
    TextView statusT;
    ImageButton imageButton;
    EditText editText;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Intent intent;

    String userid;

    MessageAdapter messageAdapter;

    List<Chat> mChat;

    ValueEventListener SeenListener;

    RecyclerView recyclerView;
    private String messageReceiverID;
   // private String saveCurrentTime, saveCurrentDate;
    private String currentUserID;
    private RequestQueue requestQueue;

    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setUpToolbar();
        setUpViews();
        RecyclerView();
        setFirebaseUser();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser= mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        userid = intent.getStringExtra("userid");

        DisplayLastSeen();

    }

    private void setFirebaseUser() {
        intent = getIntent();

        final String userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert userid != null;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                userName.setText(user.getName());

                statusT.setText(user.getStatus());

                if (user.getImage().equals("ImageUrl")) {
                    profile_pic.setImageResource(R.mipmap.ic_launcher);

                } else {
                    Glide.with(getApplicationContext()).load(user.getImage()).into(profile_pic);

                }

                RedMessages(firebaseUser.getUid(), userid, user.getImage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(final String userid) {

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        SeenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("isseen", true);

                        snapshot.getRef().updateChildren(hashMap);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    private void RecyclerView() {
        recyclerView = findViewById(R.id.chatting);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void setUpViews() {
        profile_pic = findViewById(R.id.profile_image);
        userName = findViewById(R.id.username);
        statusT = findViewById(R.id.status);
        editText = findViewById(R.id.editText);
        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(this);
    }

    private void DisplayLastSeen()
    {
        reference.child("Users").child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild("status"))
                        {
                            String state = dataSnapshot.child("status").getValue().toString();
                            String date = dataSnapshot.child("date").getValue().toString();
                            String time = dataSnapshot.child("time").getValue().toString();

                            if (state.equals("Online"))
                            {
                                statusT.setText("Online");
                            }
                            else if (state.equals("Offline"))
                            {
                                statusT.setText("Last Seen: " + date + " " + time);
                            }
                        }
                        else
                        {
                            statusT.setText("Offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void sendMessage(String sender, final String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("Time", Utilities.getCurrentDate());
        hashMap.put("Data",Utilities.getDate());

        reference.child("Chats").push().setValue(hashMap);
        final String userid = intent.getStringExtra("userid");

        assert userid != null;
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final String msg = message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (notify) {
                    assert user != null;
                    sendNotification(receiver, user.getName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String receiver, final String name, final String msg) {

        DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");

        final String userid = intent.getStringExtra("userid");

        Query query = token.orderByKey().equalTo(receiver);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid()
                            , R.drawable.img_placeholder
                            , name + ":" + msg
                            , "New Message"
                            , userid);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());

                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        Log.d("JSON_RESPONSE", "onResponse:" + response.toString());

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse:" + error.toString());

                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "kay=AAAAiMB-p_A:APA91bEbxV2tSK-U0850wDDwkbnLd8wDbkXfzjZb4uJv1aOa0Lz0qzCgSyd9w_pKCBj7sklM975oWU6VaUPAG-MXu0togzq8vnEz-P32dnzMu-yZa75JDbzYoVdx7aK8A4ixOUq8uDbh");

                                return headers;
                            }
                        };

                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        notify = true;
        String msg = editText.getText().toString();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userid = intent.getStringExtra("userid");

        if (!msg.equals("")) {
            notify = true;
            assert firebaseUser != null;
            sendMessage(firebaseUser.getUid(), userid, msg);
        } else {
            Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show();
        }
        editText.setText("");
    }

    private void RedMessages(final String MyID, final String UserId, final String ImagesUrl) {
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(MyID) && chat.getSender().equals(UserId) ||
                            chat.getReceiver().equals(UserId) && chat.getSender().equals(MyID)) {
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, ImagesUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status) {


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);


        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()) ;

        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(SeenListener);
        status("Offline");
    }
}
