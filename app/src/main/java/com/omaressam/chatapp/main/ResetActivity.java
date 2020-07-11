package com.omaressam.chatapp.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.omaressam.chatapp.R;
import com.omaressam.chatapp.auth.login.LoginActivity;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class ResetActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText sendEmail;
    private Button RESET;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        setUpToolbar();
        setUpViews();
    }

    private void setUpViews() {
        sendEmail = findViewById(R.id.send_email);
        RESET = findViewById(R.id.rest);
        RESET.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
    }

    private void setUpToolbar() {
        Toolbar toolbar7 = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar7);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public void onClick(View v) {

        final AlertDialog dialog = new SpotsDialog
                .Builder()
                .setContext(this)
                .setTheme(R.style.Custom)
                .build();
        dialog.show();

        String email = sendEmail.getText().toString();
        if (email.equals("")) {
            Toast.makeText(this, "Empty email", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else {
            auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetActivity.this, "Pleas check your email", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetActivity.this, LoginActivity.class));
                    dialog.dismiss();
                    } else {

                        String error = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(ResetActivity.this, error, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }

    }
}
