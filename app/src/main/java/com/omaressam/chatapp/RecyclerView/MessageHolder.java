package com.omaressam.chatapp.RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omaressam.chatapp.Models.ChatList;
import com.omaressam.chatapp.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

class ViewHolder extends RecyclerView.ViewHolder{


    CircleImageView picUser;

    TextView message;

    TextView seen;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        initView();

    }

    private void initView() {
        picUser = itemView.findViewById(R.id.profile_image);
        message = itemView.findViewById(R.id.show_message);
        seen = itemView.findViewById(R.id.seen);
    }
    void bindView2(ChatList chatList) {

        Picasso.get()
                .load(chatList.getUser().getImage())
                .placeholder(R.drawable.img_placeholder)
                .into(picUser);

    }
}
