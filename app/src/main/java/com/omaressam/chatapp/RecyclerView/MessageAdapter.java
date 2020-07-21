package com.omaressam.chatapp.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omaressam.chatapp.Models.Chat;
import com.omaressam.chatapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<ViewHolder> {

    private  static final int MSG_TYPE_LEFT = 0;
    private  static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageURL;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChat,String imageURL ) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){

            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(v);
        }else {

            View v = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);



        holder.bindView2(mChat.get(position));

       /* if (imageURL.equals("ImageUrl")){
            holder.picUser.setImageResource(R.drawable.img_placeholder);
        }else {
            Glide.with(mContext).load(imageURL).into(holder.picUser);
        }*/

       if (position == mChat.size()-1){
           if (chat.isIsseen()){
               holder.seen.setText("Seen");
           }else {
               holder.seen.setText("Delivered");
           }
       }else {
           holder.seen.setVisibility(View.GONE);
       }

    }



    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
