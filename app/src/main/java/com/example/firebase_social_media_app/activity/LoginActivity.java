package com.example.firebase_social_media_app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebase_social_media_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN =100 ;
    GoogleSignInClient mGoogleSignInClient;

    EditText mEmail, mPassword;
    Button mLogin;
    TextView mHaveAcount,mRecoverPass;
    SignInButton mGoogleLoginBtn;
    Toolbar toolbar;

    // progress to dispaly while registering user
    ProgressDialog progressDialog;

    // FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionToolBar();
//        // actionbar
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Login");
//
//        // enable back button
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        // before mAuth
        // default_web_client_id
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleLoginBtn=findViewById(R.id.googleLoginBtn);
        mGoogleLoginBtn.setSize(SignInButton.SIZE_STANDARD);
        //In your sign-in activity's onCreate method, get the shared instance
        mAuth = FirebaseAuth.getInstance();

        //init
        mEmail = findViewById(R.id.emailEdt);
        mPassword = findViewById(R.id.passwordEdt);
        mLogin = findViewById(R.id.loginBtn);
        mHaveAcount=findViewById(R.id.already_have);
        mRecoverPass=findViewById(R.id.recoverPass);


        // click login
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //input data
                String email= mEmail.getText().toString().trim();
                String password= mPassword.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    // invalid email
                    mEmail.setError("Invalid Email");
                    mEmail.setFocusable(true);
                }else {
                    loginUser(email,password);
                }
            }
        });
        // click goto register
        mHaveAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
            }
        });
        //recoverpass
        mRecoverPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPass();
            }
        });

        // handler google login btn click
        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // begin google login

                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Login User...");
    }



    private void showRecoverPass() {
        //AlertDialog
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        // set layput linear layout
        LinearLayout linearLayout = new LinearLayout(this);
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        /*sets the min width of A EditView */
        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);

        // button recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // input email
                String email = emailEt.getText().toString().trim();
                beginRecover(email);
            }
        });
        // button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // dismiss
                dialog.dismiss();
            }
        });

        // show dialog
        builder.create().show();
    }

    private void beginRecover(String email) {
        progressDialog.setMessage("Sending email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d("AAA",e.getMessage());
                Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loginUser(String email, String password) {
        // email and password pattern is valid, show progress dialog and start login user
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Successfull, update Ui with the sign-in user's information
                            progressDialog.show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            // show user email
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Error", "signInResult:failed code=" + e.getStatusCode());
          //  firebaseAuthWithGoogle(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    // if user is signing in first time then get and show info from google account
                    if(task.getResult().getAdditionalUserInfo().isNewUser()){
                        // get user email and uid from auth
                        String email= user.getEmail();
                        String uid = user.getUid();
                        // using HashMap
                        HashMap<Object,String> hashMap = new HashMap<>();
                        // put info trong hasmap
                        hashMap.put("email",email);
                        hashMap.put("uid",uid);
                        hashMap.put("name","");//will add later in edit profile
                        hashMap.put("onlineStatus","online");
                        hashMap.put("typingTo","noOne");//will add later in edit profile
                        hashMap.put("phone","");//will add later in edit profile
                        hashMap.put("image","");//will add later in edit profile
                        hashMap.put("cover","");//will add later in edit profile
                        // firebasedatabase instance
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference= database.getReference("Users");
                        // put data within hashmap in database
                        reference.child(uid).setValue(hashMap);
                    }

                    Toast.makeText(getApplicationContext(), "Successfull "+user.getEmail(), Toast.LENGTH_SHORT).show();
                    //go to profile activity after logged in
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();

                }else {
                    Toast.makeText(getApplicationContext(), "Error"+ task, Toast.LENGTH_SHORT).show();
                    Log.d("Error", "signInResult:failed code"+task);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Error", "Error:" + e.getMessage());
            }
        });
    }

    private void ActionToolBar() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}