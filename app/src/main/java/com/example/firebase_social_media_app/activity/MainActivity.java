package com.example.firebase_social_media_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.firebase_social_media_app.R;

public class MainActivity extends AppCompatActivity {

    //view
    Button mRegister,mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //init view
        mRegister =findViewById(R.id.register_btn);
        mLogin =findViewById(R.id.login_btn);

        //handler register button click
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start register activity
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }
}