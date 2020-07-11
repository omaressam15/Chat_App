package com.omaressam.chatapp.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omaressam.chatapp.Chats.ChatsFragment;
import com.omaressam.chatapp.Models.User;
import com.omaressam.chatapp.Profile.ProfileFragment;
import com.omaressam.chatapp.R;
import com.omaressam.chatapp.Users.UsersFragment;
import com.omaressam.chatapp.auth.login.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar ;
    private Toolbar toolbarS;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    AppBarLayout appBarLayout;
    AppBarLayout barLayout;
    private ProfileFragment profileFragment;
    private UsersFragment usersFragment;
    private ChatsFragment chatsFragment;
    private EditText Search;
    private ImageView picUsers;
    private TextView nameUsers;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    String currentUserID;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        setToolbar2();
        setToolbar();
        setFragments();
        setUpViews();

    }

    private void setToolbar2 (){
        Toolbar toolbarS = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbarS);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarS.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarLayout.setVisibility(View.VISIBLE);
                Search.setVisibility(View.GONE);
                barLayout.setVisibility(View.GONE);
            }
        });
    }

    private void setToolbar () {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        viewPager = findViewById(R.id.viewPager);
        appBarLayout = findViewById(R.id.appbar);
        barLayout = findViewById(R.id.appbar_search);
        tabLayout = findViewById(R.id.tabs);
        Search = findViewById(R.id.Search);
    }

    private void setFragments () {
        chatsFragment = new ChatsFragment();
        usersFragment = new UsersFragment();
        profileFragment = new ProfileFragment();
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(chatsFragment,"Chats");
        viewPagerAdapter.addFragment(usersFragment,"Users");
        viewPagerAdapter.addFragment(profileFragment,"Profile");
        viewPager.setAdapter(viewPagerAdapter);
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();


        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);

        }

        void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitle.add(title);

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }

    private void setUpViews() {
        picUsers =  findViewById(R.id.profile_image);
        nameUsers = findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        setFirebase();
    }

    private void setFirebase () {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                nameUsers.setText(user.getName());

                if (user.getImage().equals("ImageUrl")){
                    picUsers.setImageResource(R.mipmap.ic_launcher);

                }else {
                    Glide.with(getApplicationContext()).load(user.getImage()).into(picUsers);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout) {

            mAuth = FirebaseAuth.getInstance();

            currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

            FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
            if(mFirebaseUser != null) {
                currentUserID = mFirebaseUser.getUid(); //Do what you need to do with the id

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                return true;
            }
        }

        if (item.getItemId() == R.id.search){
            appBarLayout.setVisibility(View.GONE);
            Search.setVisibility(View.VISIBLE);
            barLayout.setVisibility(View.VISIBLE);
        }


        return false;
    }


    private void status(String status ) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }
}
