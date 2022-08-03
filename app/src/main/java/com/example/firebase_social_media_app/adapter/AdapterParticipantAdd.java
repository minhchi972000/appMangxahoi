package com.example.firebase_social_media_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
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

import com.example.firebase_social_media_app.R;
import com.example.firebase_social_media_app.model.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;


public class AdapterParticipantAdd extends RecyclerView.Adapter<AdapterParticipantAdd.MyViewHolder> {
    Activity context;
    ArrayList<ModelUser> userList;
    String groupId, myGroupRole;//creator/admin/participant

    public AdapterParticipantAdd(Activity context, ArrayList<ModelUser> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_participant_add, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //get data
        ModelUser modelUser = userList.get(position);
        String name = modelUser.getName();
        String email = modelUser.getEmail();
        String image = modelUser.getImage();
        String uid = modelUser.getUid();

        //set data
        holder.nameTv.setText(name);
        holder.emailTv.setText(email);
        try {
            Picasso.get().load(image).placeholder(R.drawable.user).into(holder.avartaIv);
        } catch (Exception e) {
            holder.avartaIv.setImageResource(R.drawable.user);
        }
        checkIfAlreadyExists(modelUser, holder);

        //handler click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* check if user already added or not
                 * if added: show remove-participant/make admin/remove admin option(admin will not able to change role of creator
                 * if not added , show add participant option*/
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    //user exists/ not - participant
                                    String hisPreviousRole = "" + snapshot.child("role").getValue();
                                    //options to display in dialog
                                    String[] options;
                                    Log.d("myGroupRoleAdapter",myGroupRole);
                                    Log.d("myGroupRoleAdapter",hisPreviousRole);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Option");
                                    if (myGroupRole.equals("creator")) {
                                        if (hisPreviousRole.equals("admin")) {
                                            // I'm creator, he is admin
                                            Toast.makeText(context, "myGroupRoleAdapter", Toast.LENGTH_SHORT).show();
                                            options = new String[]{"Remove admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //handle item clicks
                                                    if (i == 0) {
                                                        //remove admin
                                                        removeAdmin(modelUser);
                                                    } else {
                                                        //remove user
                                                        removeParticipant(modelUser);
                                                    }
                                                }
                                            }).show();
                                        } else if (hisPreviousRole.equals("participant")) {
                                            // I'm creator, he is participant
                                            options = new String[]{"Make admin", "Remove user"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //handle item clicks
                                                    if (i == 0) {
                                                        //Make admin
                                                        makeAdmin(modelUser);
                                                    } else {
                                                        //remove user
                                                        removeParticipant(modelUser);

                                                    }

                                                }
                                            }).show();
                                        }
                                    }
                                    else if (myGroupRole.equals("admin")) {
                                        if (hisPreviousRole.equals("creator")) {
                                            // im admin, he is creator
                                            Toast.makeText(context, "Creator of group...", Toast.LENGTH_SHORT).show();
                                        } else if (hisPreviousRole.equals("admin")) {
                                            // im admin, he is admin too
                                            options = new String[]{"Remove admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //handle item clicks
                                                    if (i == 0) {
                                                        //remove admin
                                                        removeAdmin(modelUser);
                                                    } else {
                                                        //remove user
                                                        removeParticipant(modelUser);
                                                    }
                                                }
                                            }).show();
                                        } else if (hisPreviousRole.equals("participant")) {
                                            //im admin, he is participant
                                            options = new String[]{"Make admin", "Remove User"};
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //handle item clicks
                                                    if (i == 0) {
                                                        //rMake admin
                                                        makeAdmin(modelUser);
                                                    } else {
                                                        //remove user
                                                        removeParticipant(modelUser);
                                                    }
                                                }
                                            }).show();
                                        }
                                    }
                                } 
                                else {
                                    // user doesn't exists / not participant: add
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participant")
                                            .setMessage("Add this user in this group ?")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    // add user
                                                    addParticipant(modelUser);
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }

    private void addParticipant(ModelUser modelUser) {
        //setup user data - add user in group
        String timestamp =""+System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid",modelUser.getUid());
        hashMap.put("role","participant");
        hashMap.put("timestamp",timestamp);
        //add that user in Group >GroupId>Participants
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //added successfully
                        Toast.makeText(context, "Added succesfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeAdmin(ModelUser modelUser) {
        //setup data - change role
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("role","admin");// role are participant/admin/creator
        //update role in db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //made admin
                        Toast.makeText(context, "The user is now admin...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void removeParticipant(ModelUser modelUser) {
        // remove participant from groups
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //remove successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void removeAdmin(ModelUser modelUser) {
        //setup data - remove admin- just change role
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("role","participant");// role are participant/admin/creator
        //update role in db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //made admin
                        Toast.makeText(context, "The user is no longer...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void checkIfAlreadyExists(ModelUser modelUser, MyViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //đã tồn tại:already exists
                            String hisRole = "" + snapshot.child("role").getValue();// role : tao ra
                            holder.statusTv.setText(hisRole);

                        } else {
                            //doesn't exists
                            holder.statusTv.setText("");

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView avartaIv;
        TextView emailTv, nameTv, statusTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            avartaIv = itemView.findViewById(R.id.avartaIv);
            emailTv = itemView.findViewById(R.id.emailTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }
}
