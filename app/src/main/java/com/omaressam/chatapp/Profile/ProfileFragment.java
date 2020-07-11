package com.omaressam.chatapp.Profile;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.omaressam.chatapp.Models.User;
import com.omaressam.chatapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {


    private TextView nameProfile;
    private TextView emailProfile;
    private ImageView imageProfile;
    private Button buttonProfile;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private Uri imageUri;
    private static final int IMAGE_REQUEST = 200;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setUpViews(view);
        get();
        return view;

    }

    private void setUpViews(View view) {

        nameProfile = view.findViewById(R.id.username);

        emailProfile = view.findViewById(R.id.profile_email_textView);

        imageProfile = view.findViewById(R.id.profile_image);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

    }

    private void get() {

        myRef = database.getReference("Users").child(user.getUid());

        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userObject = dataSnapshot.getValue(User.class);
                assert userObject != null;
                nameProfile.setText(userObject.getName());
                emailProfile.setText(userObject.getEmail());

                Picasso.get()
                        .load(userObject.getImage())
                        .placeholder(R.drawable.img_placeholder)
                        .into(imageProfile);

                //  savePostToDB(userObject);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.toException(), Toast.LENGTH_SHORT).show();
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    private String getFileExtension (Uri uri){

        ContentResolver contentResolver = requireContext().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImage () {
        final AlertDialog dialog = new SpotsDialog
                .Builder()
                .setContext(getContext())
                .setTheme(R.style.Uploading)
                .build();
        dialog.show();

        if (imageUri!=null){
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    +"-" + getFileExtension(imageUri));


            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation <UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task <UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener <Uri> () {
                @Override
                public void onComplete(@NonNull Task <Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        String mUri = downloadUri.toString();

                        myRef = database.getReference("Users").child(user.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("image",mUri);
                        myRef.updateChildren(map);
                        dialog.dismiss();

                    }else {
                        Toast.makeText(getContext(), "Filed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage() , Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==IMAGE_REQUEST && resultCode == RESULT_OK  && data !=null && data.getData() !=null ){
            imageUri = data.getData();
            if (uploadTask !=null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress ", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
            }
        }

    }
}
