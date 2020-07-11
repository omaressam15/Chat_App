package com.omaressam.chatapp.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omaressam.chatapp.Models.User;
import com.omaressam.chatapp.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

class UserHolder extends RecyclerView.ViewHolder {

     ImageView picUsers;
     TextView nameUsers;
     CircleImageView online;
     CircleImageView offline;
     TextView last_mag;

    public UserHolder(@NonNull View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        picUsers = itemView.findViewById(R.id.profile_image);
        nameUsers = itemView.findViewById(R.id.username);
        last_mag = itemView.findViewById(R.id.last_mass);
        online = itemView.findViewById(R.id.image_on);
        offline = itemView.findViewById(R.id.image_of);

    }

    void bindView(User user) {

        Picasso.get()
                .load(user.getImage())
                .placeholder(R.drawable.img_placeholder)
                .into(picUsers);

        nameUsers.setText(user.getName());
    }
}
