package com.example.firebase_social_media_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import com.example.firebase_social_media_app.R;
import com.example.firebase_social_media_app.activity.ChatActivity;
import com.example.firebase_social_media_app.activity.ThereProfileActivity;
import com.example.firebase_social_media_app.model.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyViewHolder> {

    //getting current user's uid
    FirebaseAuth firebaseAuth;
    String myUid;

    /*:Error:  Unable to add window -- token null is not valid; is your activity running? or
    AlertDialog
    * */
    Activity context;
    List<ModelUser> userList;

    public AdapterUsers(Activity context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // inflate layout row_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // get data
        String hisUID = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String userEmail = userList.get(position).getEmail();


        //set data
        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);


        try {
            Picasso.get().load(userImage).placeholder(R.drawable.user).into(holder.mAvatarTv);

        } catch (Exception e) {
            Log.d("Error adapter:", e.getMessage());
            Toast.makeText(context, "Error adapter" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        holder.blockIv.setImageResource(R.drawable.ic_unblock);
        //check if each user if is blocked or not
        checkIsBlocked(hisUID, holder, position);
        // handler item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                // show AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Chats"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            // profile click
                            Intent intent = new Intent(context, ThereProfileActivity.class);
                            intent.putExtra("uid", hisUID);
                            context.startActivity(intent);
                        }
                        if (i == 1) {
                            // chat licked
                            /* use UId of recriver and identify the user we are gonna chat
                             */
                            imBlockedORNot(hisUID);

                        }
                    }
                });
                builder.create().show();
//                new AlertDialog.Builder(context)
//                        .setItems(new String[]{"Profile", "Chats"}, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                switch (i) {
//                                    case 0:
//                                        //  profile click
//                                        Intent intent = new Intent(context, ThereProfileActivity.class);
//                                        intent.putExtra("uid", hisUID);
//                                        context.startActivity(intent);
//                                        break;
//
//                                    case 1:
//                                        // chat licked
////                            /* use UId of recriver and identify the user we are gonna chat
////                             */
//                                        imBlockedORNot(hisUID);
//                                        break;
//                                }
//                            }
//                        })
//                        .show();

            }
        });
        //click to block user
        holder.blockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userList.get(holder.getAdapterPosition()).isBlocked()) {
                    unBlockUser(hisUID);
                } else {
                    blockUser(hisUID);
                }

            }
        });
    }

    private void imBlockedORNot(String hisUID) {
        //first check if sender ( current user) is blocked by receiver or not
        // if uid of the sender exists in "BlockedUsers" of receiver then sender(current user) is blocked, otherwise not
        // if blocked then display a message . You're blocked by that user, can't send message
        // if not blocked then simply start th chat activity
        //if uid of the user exists in "BlockedUsers" then that user is blocked, otherwise not
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUID).child("BlockedUsers").orderByChild("uid").equalTo(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.exists()) {
                                Toast.makeText(context, "You're blocked by that user, can't send message", Toast.LENGTH_SHORT).show();
                                //blocked, dont proceed further
                                return;
                            }

                        }
                        // not blocked,start activity
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("hisUid", hisUID);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkIsBlocked(String hisUID, MyViewHolder holder, int position) {
        //check if each user if is blocked or not
        //if uid of the user exists in "BlockedUsers" then that user is blocked, otherwise not
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").orderByChild("uid").equalTo(hisUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.exists()) {
                                holder.blockIv.setImageResource(R.drawable.ic_block_black);
                                userList.get(holder.getAdapterPosition()).setBlocked(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void blockUser(String hisUID) {
        // block user , by adding uid to current user's "BlockedUser's" node
        //Put values in hasmap to put in db
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", hisUID);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").child(hisUID).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //block successfully
                        Toast.makeText(context, "Blocked successfully...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unBlockUser(String hisUID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").orderByChild("uid").equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.exists()) {
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // unblocked successfully
                                                Toast.makeText(context, "Unblock successfully..", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mAvatarTv;
        ImageView blockIv;
        TextView mNameTv, mEmailTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mAvatarTv = itemView.findViewById(R.id.avartaIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
            blockIv = itemView.findViewById(R.id.blockIv);


        }
    }
}
