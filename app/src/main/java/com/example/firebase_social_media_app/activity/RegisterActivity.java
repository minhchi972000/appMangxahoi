package com.example.firebase_social_media_app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_social_media_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //view
    EditText mEmail, mPassword;
    Button mRegister;
    TextView mHaveAcount;
    Toolbar toolbar;

    // progress to dispaly while registering user
    ProgressDialog progressDialog;

    // FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // actionbar
        ActionToolBar();
////        ActionBar actionBar = getSupportActionBar();
////        actionBar.setTitle("Create Account");
//        // enable back button
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        // init
        mEmail =findViewById(R.id.emailEdt);
        mPassword =findViewById(R.id.passwordEdt);
        mRegister =findViewById(R.id.registerBtn);
        mHaveAcount =findViewById(R.id.already_have);

        //In your sign-in activity's onCreate method, get the shared instance
        mAuth = FirebaseAuth.getInstance();

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Register User...");

        //handler button click
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // input email, password
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                //validate
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    // set error and foucuss to email edit
                    mEmail.setError("Invalid Email");
                    mEmail.setFocusable(true);
                }else if(password.length()<6){
                    // set error and foucuss to password edit
                    mPassword.setError("Pass lenght at least 6 characters");
                    mPassword.setFocusable(true);
                }else {
                    registerUser(email,password); // register user
                }
            }
        });

        mHaveAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

    }

    private void registerUser(String email, String password) {
        // email and password pattern is valid, show progress dialog and start registering user
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            // Successfull, update Ui with the sign-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            // get user email and uid from auth
                            String email= user.getEmail();
                            String uid = user.getUid();
                            // using HashMap
                            HashMap<Object,String> hashMap = new HashMap<>();
                            // put info trong hasmap
                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
                            hashMap.put("name","");//will add later in edit profile
                            hashMap.put("onlineStatus","online");//will add later in edit profile
                            hashMap.put("typingTo","noOne");//will add later in edit profile
                            hashMap.put("phone","");//will add later in edit profile
                            hashMap.put("image","");//will add later in edit profile
                            hashMap.put("cover","");//will add later in edit profile
                            // firebasedatabase instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference= database.getReference("Users");
                            // put data within hashmap in database
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(getApplicationContext(), "Successfull "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            finish();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();// go previous acticity
        return super.onSupportNavigateUp();
    }
    private void ActionToolBar() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}