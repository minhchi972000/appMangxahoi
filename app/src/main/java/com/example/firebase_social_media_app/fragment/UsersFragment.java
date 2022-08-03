package com.example.firebase_social_media_app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase_social_media_app.R;
import com.example.firebase_social_media_app.activity.GroupCreateActivity;
import com.example.firebase_social_media_app.activity.MainActivity;
import com.example.firebase_social_media_app.activity.SettingsActivity;
import com.example.firebase_social_media_app.adapter.AdapterUsers;
import com.example.firebase_social_media_app.model.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class UsersFragment extends Fragment {

    //firebase auth
    FirebaseAuth mAuth;
    FirebaseUser fUser;


    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;


    public UsersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        //init
        mAuth = FirebaseAuth.getInstance();

        // init recyclerview
        recyclerView = view.findViewById(R.id.users_recyclerView);
        //set properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        // init user list
        userList = new ArrayList<>();


        // getall users
        getAllUsers();

        return view;
    }

    private void getAllUsers() {
//        // get current user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        // get path of database name " Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        userList.add(modelUser);

                    }
                    Log.d("Suceess: ", userList.toString());
                    // adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);

                    //set adapter to recycview
                    recyclerView.setAdapter(adapterUsers);
                    adapterUsers.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error fail users:", error.getMessage());
                Toast.makeText(getActivity(), "Error fail users:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchUser(String query) {
        // get current user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        // get path of database name " Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all search from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(modelUser);
                            //  Toast.makeText(getActivity(), "Loading users Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Log.d("Suceess: ", userList.toString());
                    // adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);

                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recycview
                    recyclerView.setAdapter(adapterUsers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error fail users:", error.getMessage());
                Toast.makeText(getActivity(), "Error fail users:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void checkUserStatus() {
        //get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // user is signed in stay here
            // set email of logged in user
            // mProfile.setText(user.getEmail());


        } else {

            // user not signed in, go to mainActivity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show option in fragment
        super.onCreate(savedInstanceState);
    }

    // optons menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        // hide addpost icon from this  fragment
        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_add_participant).setVisible(false);
        menu.findItem(R.id.action_groupinfo).setVisible(false);

        // Search view
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        // search listiner
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // called when user press search button from keyboard
                if (!TextUtils.isEmpty(s.trim())) {
                    //search text contains text, search it
                    searchUser(s);

                } else {
                    // serach text empty, get all user
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // called when user press search button from keyboard
                if (!TextUtils.isEmpty(s.trim())) {
                    //search text contains text, search it
                    searchUser(s);

                } else {
                    // serach text empty, get all user
                    getAllUsers();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    // click item
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // swich case
        switch (id) {
            case R.id.action_logout:
                mAuth.signOut();
                checkUserStatus();
                break;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.action_create_group:
                startActivity(new Intent(getActivity(), GroupCreateActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}