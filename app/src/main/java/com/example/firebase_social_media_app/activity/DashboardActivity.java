package com.example.firebase_social_media_app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.firebase_social_media_app.R;
import com.example.firebase_social_media_app.fragment.ChatListFragment;
import com.example.firebase_social_media_app.fragment.GroupChatsFragment;
import com.example.firebase_social_media_app.fragment.HomeFragment;
import com.example.firebase_social_media_app.fragment.NotificationsFragment;
import com.example.firebase_social_media_app.fragment.ProfileFragment;
import com.example.firebase_social_media_app.fragment.UsersFragment;
import com.example.firebase_social_media_app.notications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DashboardActivity extends AppCompatActivity {
    //firebase auth
    FirebaseAuth mAuth;
    String mUID;
    //  ActionBar actionBar;
    BottomNavigationView navigationView;
    Toolbar actionBar;
    private static final int FRAGMENT_HOME = 1;
    private static final int FRAGMENT_PROFILE = 2;
    private static final int FRAGMENT_USERS = 3;
    private static final int FRAGMENT_CHATS = 4;
    private static final int FRAGMENT_NOTIFICATIONS = 5;
    private static final int FRAGMENT_GROUPCHATS = 6;


    private int currentFragment = FRAGMENT_HOME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // actionbar
        actionBar = findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new HomeFragment();

        //init
        mAuth = FirebaseAuth.getInstance();

        //bottom navigation
        navigationView = findViewById(R.id.bottomnavigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        navigationView.setItemIconTintList(null);

        replaceFragment(new HomeFragment());

        checkUserStatus();


    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    private void replaceFragment(Fragment fragment) {
        actionBar.setTitle("Home");
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.container, fragment, "");
        ft1.commit();
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
//                    // home fragment transaction
//                    actionBar.setTitle("Home");
//                    HomeFragment fragment1 = new HomeFragment();
//                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//                    ft1.replace(R.id.container, fragment1, "");
//                    navigationView.getMenu().findItem(android.R.color.holo_orange_dark);
//                    //  navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
//                    ft1.commit();
                    openHomeFragment();
                    return true;

                case R.id.nav_profile:
                    // profile fragment transaction
//                    actionBar.setTitle("Profile");
//                    ProfileFragment fragment2 = new ProfileFragment();
//                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
//                    ft2.replace(R.id.container, fragment2, "");
//                    navigationView.getMenu().findItem(android.R.color.holo_orange_dark);
//                    // navigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
//                    ft2.commit();
                    openProfileFragment();
                    return true;

                case R.id.nav_users:
                    openUsersFragment();
                    return true;

                case R.id.nav_chat:
                    // user fragment transaction
                    openChatsFragment();
                    return true;

                case R.id.nav_more:
                    // user fragment transaction
                    openNotificationAndGroupFragment();
                    return true;
            }
            return false;
        }
    };


    private void checkUserStatus() {
        //get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // user is signed in stay here
            // set email of logged in user
            // mProfile.setText(user.getEmail());
            // startActivity(new Intent(getApplicationContext(), HomeFragment.class));
            mUID = user.getUid();
            // save uid of currently signed user in shared preferences
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

//            //update token
//            updateToken(FirebaseInstanceId.getInstance().getToken());

        } else {
            // user not signed in, go to mainActivity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Toast.makeText(this, "Vui long dang nhap", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void openHomeFragment() {
        if (currentFragment != FRAGMENT_HOME) {
            // home fragment transaction
            actionBar.setTitle("Home");
            HomeFragment fragment1 = new HomeFragment();
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.replace(R.id.container, fragment1, "");
            navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
            ft1.commit();
            currentFragment = FRAGMENT_HOME;
        }
    }

    private void openProfileFragment() {
        if (currentFragment != FRAGMENT_PROFILE) {
            actionBar.setTitle("Profile");
            ProfileFragment fragment2 = new ProfileFragment();
            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
            ft2.replace(R.id.container, fragment2, "");
            navigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
            ft2.commit();
            currentFragment = FRAGMENT_PROFILE;
        }
    }

    private void openUsersFragment() {
        if (currentFragment != FRAGMENT_USERS) {
            // user fragment transaction
            actionBar.setTitle("Users");
            UsersFragment fragment3 = new UsersFragment();
            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
            ft3.replace(R.id.container, fragment3, "");
            navigationView.getMenu().findItem(R.id.nav_users).setChecked(true);
            ft3.commit();
            currentFragment = FRAGMENT_USERS;
        }
    }

    private void openChatsFragment() {
        if (currentFragment != FRAGMENT_CHATS) {
            actionBar.setTitle("Chast");
            ChatListFragment fragment4 = new ChatListFragment();
            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
            ft4.replace(R.id.container, fragment4, "");
            navigationView.getMenu().findItem(R.id.nav_chat).setChecked(true);
            ft4.commit();
            currentFragment = FRAGMENT_CHATS;
        }
    }

    private void openNotificationAndGroupFragment() {
        if (currentFragment != FRAGMENT_NOTIFICATIONS) {
            //popup menu to show mare options
            PopupMenu popupMenu = new PopupMenu(this, navigationView, Gravity.END);
            //item to show in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Notifications");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Group Chats");
            //menu click
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id) {
                        case 0:
                            //notification click
                            actionBar.setTitle("Notifications");
                            NotificationsFragment fragment5 = new NotificationsFragment();
                            FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                            ft5.replace(R.id.container, fragment5, "");
                            navigationView.getMenu().findItem(R.id.nav_chat).setChecked(true);
                            ft5.commit();
                            currentFragment = FRAGMENT_NOTIFICATIONS;
                            break;
                        case 1:
                            //Group chats click
                            actionBar.setTitle("Group Chats");
                            GroupChatsFragment fragment6 = new GroupChatsFragment();
                            FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
                            ft6.replace(R.id.container, fragment6, "");
                            navigationView.getMenu().findItem(R.id.nav_chat).setChecked(true);
                            ft6.commit();
                            currentFragment = FRAGMENT_GROUPCHATS;
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }


    @Override
    protected void onStart() {
        // check on start of app
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}