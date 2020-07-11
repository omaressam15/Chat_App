package com.omaressam.chatapp.auth.register;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.omaressam.chatapp.Models.User;
import com.omaressam.chatapp.R;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName,email,password ;
    private ImageView imageView;
    private Button createAccount;
    private FirebaseAuth mAuth;
    private Toolbar toolbar ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUpViews();
        setUpActionBar();
    }

    private void setUpViews(){

        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        imageView = findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();

        createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);
    }

    private void setUpActionBar () {
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ChatApp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void registerUsers () {


        String Username = userName.getText().toString();
        String Email = email.getText().toString();
        String Password = password.getText().toString();


        if (TextUtils.isEmpty(Username)) {
            userName.setError("Enter your name");
            Toast.makeText(this, "Please enter yor name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Email) || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Invalid Email ");
            email.setFocusable(true);
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            //stopping execution further
            return;
        }

        if (Password.length()<6 ||TextUtils.isEmpty(Password) ) {
            password.setError("password length at least 6 characters");
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping execution further
            return;
        }


        User user = new User();
        user.setName(Username);
        user.setEmail(Email);
        user.setPassword(Password);

        createUser(user);

    }

    private void createUser(final User user) {
        final AlertDialog dialog = new SpotsDialog
                .Builder()
                .setContext(this)
                .setTheme(R.style.Custom)
                .build();
        dialog.show();

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.show();
                            User user1 = new User();

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;

                            user1.setId(firebaseUser.getUid());

                            saveUsers(firebaseUser.getUid(),user,password.getText().toString(),userName.getText().toString());


                            dialog.dismiss();
                        } else {

                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void saveUsers (String id,User user ,String password,String username ) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(id);
        user.setPassword(password);
        user.setId(id);
        user.setSearch(username.toLowerCase());
        user.setImage("ImageUrl");
        myRef.setValue(user);
        Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onClick(View v) {
        registerUsers();
    }
}
