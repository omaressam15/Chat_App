package com.omaressam.chatapp.RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omaressam.chatapp.Models.Chat;
import com.omaressam.chatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

class ViewHolder extends RecyclerView.ViewHolder{


    CircleImageView picUser;

    TextView time;

    TextView message;

    TextView Data;

    TextView seen;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        initView();

    }

    private void initView() {
        picUser = itemView.findViewById(R.id.profile_image);
        message = itemView.findViewById(R.id.show_message);
        seen = itemView.findViewById(R.id.seen);
        time= itemView.findViewById(R.id.time);
        Data = itemView.findViewById(R.id.Data);

    }
    void bindView2(Chat chat) {

        message.setText(chat.getMessage());

        time.setText(chat.getTime());

        Data.setText(chat.getData());

    }
}

