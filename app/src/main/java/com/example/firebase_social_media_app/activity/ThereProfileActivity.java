package com.example.firebase_social_media_app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_social_media_app.R;
import com.example.firebase_social_media_app.adapter.AdapterPosts;
import com.example.firebase_social_media_app.model.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThereProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    // View from xml
    CircleImageView avartaTv;
    ImageView coverTv;
    TextView nametv, emailtv, phonetv;
    Toolbar toolbar;

    RecyclerView postsRecyclerview;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Profile");
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        //init view
        avartaTv = findViewById(R.id.avartaTv);
        nametv = findViewById(R.id.nametv);
        emailtv =findViewById(R.id.emailtv);
        phonetv = findViewById(R.id.phonetv);
        coverTv = findViewById(R.id.coverTv);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        // enable back button in actionbar
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postsRecyclerview =findViewById(R.id.recyclerview_posts);
        mAuth = FirebaseAuth.getInstance();

        //get uid of clicked user to retrieve his post
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check until reqyured data get
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // get data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    //set data
                    nametv.setText(name);
                    emailtv.setText(email);
                    phonetv.setText(phone);

                    try {
                        // set image
                        Picasso.get().load(image).into(avartaTv);
                    } catch (Exception e) {
                        // error
                        Picasso.get().load(R.drawable.user).into(avartaTv);
                    }
                    try {
                        // set image

                        Picasso.get().load(cover).into(coverTv);
                    } catch (Exception e) {
                        // error
                        Picasso.get().load(R.drawable.user).into(coverTv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postList = new ArrayList<>();

        checkUserStatus();
        loadHisPosts();
    }

    private void loadHisPosts() {
        // Linearlayout recyclerview
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        // show newest post first, for this load from last
        layoutManager.setStackFromEnd(true); // new story from last
        layoutManager.setReverseLayout(true);
        // set layout
        postsRecyclerview.setLayoutManager(layoutManager);

        // init post list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        // query to load posts
        Query query= ref.orderByChild("uid").equalTo(uid);
        // get all data from ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPost myPosts= ds.getValue(ModelPost.class);
                    // add to list
                    postList.add(myPosts);
                    // adapter
                    adapterPosts = new AdapterPosts(ThereProfileActivity.this,postList);
                    // set recyclerview
                    postsRecyclerview.setAdapter(adapterPosts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ThereProfileActivity.this,"ErrorProfileFragment "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchHisPosts(String searchQuery) {
        // Linearlayout recyclerview
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        // show newest post first, for this load from last
        layoutManager.setStackFromEnd(true); // new story from last
        layoutManager.setReverseLayout(true);
        // set layout
        postsRecyclerview.setLayoutManager(layoutManager);

        // init post list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        // query to load posts
        Query query= ref.orderByChild("uid").equalTo(uid);
        // get all data from ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPost myModelPost= ds.getValue(ModelPost.class);
                    if(myModelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            myModelPost.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())){
                        // add to list
                        postList.add(myModelPost);
                    }
                    // adapter
                    adapterPosts = new AdapterPosts(getApplicationContext(),postList);
                    // set recyclerview
                    postsRecyclerview.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"ErrorProfileFragment "+error.getMessage(),Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_add_post).setVisible(false); // hide add post
        // hide addpost icon from this  fragment
        menu.findItem(R.id.action_create_group).setVisible(false);

        MenuItem item = menu.findItem(R.id.action_search);
        // searchView
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s)) {
                    // search
                    searchHisPosts(s);
                } else {
                    loadHisPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s)) {
                    // search
                    searchHisPosts(s);
                } else {
                    loadHisPosts();
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // swich case
        switch (id) {
            case R.id.action_logout:
                mAuth.signOut();
                checkUserStatus();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}