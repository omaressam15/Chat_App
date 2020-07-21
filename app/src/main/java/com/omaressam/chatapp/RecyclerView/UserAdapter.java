package com.omaressam.chatapp.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omaressam.chatapp.Models.Chat;
import com.omaressam.chatapp.Models.User;
import com.omaressam.chatapp.R;
import com.omaressam.chatapp.main.MessageActivity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean onOrOf;
    private String theLastMassage;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    String currentUserID;


    public UserAdapter(Context mContext, List<User> mUsers, boolean onOrOf) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.onOrOf = onOrOf;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, final int position) {

        final User user = mUsers.get(position);
        holder.bindView(mUsers.get(position));

        if (onOrOf){
            lastMessage(user.getId(),holder.last_mag);
        }else {
            holder.last_mag.setVisibility(View.GONE);
        }

        if (onOrOf) {
            if (user.getStatus().equals("Online")) {
                holder.online.setVisibility(View.VISIBLE);
                holder.offline.setVisibility(View.GONE);
            } else {
                holder.online.setVisibility(View.GONE);
                holder.offline.setVisibility(View.VISIBLE);
            }
        } else {
            holder.online.setVisibility(View.GONE);
            holder.offline.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                mContext.startActivity(intent);

            }
        });

    }

    private void lastMessage(final String userId, final TextView last_mas) {

        theLastMassage = "default";

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Chat chat = snapshot.getValue(Chat.class);

                    mAuth = FirebaseAuth.getInstance();

                    FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
                    assert chat != null;
                    assert firebaseUser != null;
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)
                    || chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {

                        theLastMassage = chat.getMessage();

                    }else {

                        if(mFirebaseUser == null){

                            Toast.makeText(mContext, "sdfds", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
               switch (theLastMassage){
                   case "default":
                       last_mas.setText("");
                       break;

                   default:
                       last_mas.setText(theLastMassage);
                       break;

               }
                theLastMassage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}