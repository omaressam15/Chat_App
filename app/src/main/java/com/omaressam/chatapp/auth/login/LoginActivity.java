package com.omaressam.chatapp.auth.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omaressam.chatapp.R;
import com.omaressam.chatapp.auth.register.RegisterActivity;
import com.omaressam.chatapp.main.MainActivity;
import com.omaressam.chatapp.main.ResetActivity;

import dmax.dialog.SpotsDialog;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText Email , Password;
    private Button LogIN , SingUP;
    private FirebaseAuth mAuth;
    private TextView forgot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SetUpView();
        userLoginOrNot();
        setUpActionBar();

    }
    private void SetUpView () {
        Email = findViewById(R.id.email1);
        Password = findViewById(R.id.pass);
        LogIN = findViewById(R.id.login);
        SingUP = findViewById(R.id.singUp);
        forgot = findViewById(R.id.forgotPassword);
        mAuth = FirebaseAuth.getInstance();
        SingUP.setOnClickListener(this);
        LogIN.setOnClickListener(this);
        forgot.setOnClickListener(this);
    }

    private void userLoginOrNot () {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void setUpActionBar () {
        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create Account");
        toolbar.setCollapseIcon(R.drawable.whatsapp);

    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){
            case R.id.singUp :
                startActivity(new Intent(this, RegisterActivity.class));
            break;

            case R.id.login:
                registerUsers();

            break;

            case R.id.forgotPassword:
                startActivity(new Intent(this, ResetActivity.class));
        }
    }
    private void registerUsers () {

        String email = Email.getText().toString();
        String password = Password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Email.setError("Enter your email");
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Password.setError("Enter your password");
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping execution further
            return;
        }
        signIn(email, password);

    }
    private void signIn (final String email, final String password) {
        final AlertDialog dialog = new SpotsDialog
                .Builder()
                .setContext(this)
                .setTheme(R.style.Custom)
                .build();
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.show();
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            dialog.dismiss();

                        } else {

                            Toast.makeText(LoginActivity.this, "Wrong email and password!",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

}
