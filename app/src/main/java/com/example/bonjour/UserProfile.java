package com.example.bonjour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bonjour.fragments.ExploreFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static com.example.bonjour.R.drawable.default_user;
import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class UserProfile extends AppCompatActivity {
    TextView name_tv,mutual_tv,relation_tv;
    Button option1_bt,option2_bt;
    CircleImageView profileimage;
    final String  U = getInstance().getCurrentUser().getEmail();
    String doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        final Intent intent=getIntent();
        getchatdocument(U,intent.getStringExtra("profile_id"));
        name_tv=findViewById(R.id.profile_user_tv);
        mutual_tv=findViewById(R.id.friends_mutual_tv);
        option1_bt=findViewById(R.id.profile_option1_bt);
        option2_bt=findViewById(R.id.profile_option2_bt);
        relation_tv=findViewById(R.id.profile_relation_tv);
        profileimage=findViewById(R.id.circleImageView);

        name_tv.setText(intent.getStringExtra("profilename"));
        mutual_tv.setText(intent.getStringExtra("profilestatus"));
        Picasso.get().load(intent.getStringExtra("profileimage")).placeholder(default_user).into(profileimage);



        FirebaseFirestore.getInstance().collection("requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {      option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        option1_bt.setBackgroundColor(getResources().getColor(R.color.coloroption));
                        option1_bt.setVisibility(View.VISIBLE);
                        option2_bt.setVisibility(View.INVISIBLE);
                        relation_tv.setText("Not Friends");
                        option1_bt.setText("Send Friend Request");
                        option1_bt.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                sendRequestToDb(U,intent.getStringExtra("profile_id"));
                            }
                        });
                        for (QueryDocumentSnapshot document : value) {




                        if(document.getId().contains(U)&&document.getId().contains(intent.getStringExtra("profile_id"))){
                            if(document.getData().get("status").equals("friends")){
                                option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                option1_bt.setVisibility(View.VISIBLE);
                                option2_bt.setVisibility(View.INVISIBLE);
                                option1_bt.setBackgroundColor(getResources().getColor(R.color.colorerror));
                                relation_tv.setText("Already Friends");
                                option1_bt.setText("Unfriend" );
                                option1_bt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        unfriendFromDb(U,intent.getStringExtra("profile_id"));
                                    }
                                });
                            }
                            else if (document.getData().get("from").equals(U)){
                                option1_bt.setTextColor(getResources().getColor(R.color.colorAccent));
                                option1_bt.setVisibility(View.VISIBLE);
                                option2_bt.setVisibility(View.INVISIBLE);
                                option1_bt.setBackgroundColor(getResources().getColor(R.color.colordisabled));
                                relation_tv.setText("You sent Friend Request");
                                option1_bt.setText("Cancel Request");
                                option1_bt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cancelRequestFromDb(U,intent.getStringExtra("profile_id"));
                                    }
                                });
                            }
                            else{
                                option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                option2_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                option1_bt.setVisibility(View.VISIBLE);
                                option2_bt.setVisibility(View.VISIBLE);
                                option1_bt.setBackgroundColor(getResources().getColor(R.color.colorverify));
                                relation_tv.setText(intent.getStringExtra("profilename") +" sent You Request");
                                option1_bt.setText("Accept Request");
                                option2_bt.setBackgroundColor(getResources().getColor(R.color.colordisabled));
                                option2_bt.setText("Ignore Request");
                                option1_bt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        addFriendToDb(U,intent.getStringExtra("profile_id"));
                                    }
                                });
                                option2_bt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ignoreFriendRequest(U,intent.getStringExtra("profile_id"));
                                    }
                                });

                            }
                         }
                        }
                    }
                });         }

    private void ignoreFriendRequest(final String user, final String profileemail) {
        FirebaseFirestore.getInstance().collection("requests").document(profileemail+user).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        option1_bt.setBackgroundColor(getResources().getColor(R.color.coloroption));
                        option1_bt.setVisibility(View.VISIBLE);
                        option2_bt.setVisibility(View.INVISIBLE);
                        relation_tv.setText("Not Friends");
                        option1_bt.setText("Send Friend Request");
                        option1_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendRequestToDb(user,profileemail);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                option2_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                option1_bt.setVisibility(View.VISIBLE);
                option2_bt.setVisibility(View.VISIBLE);
                option1_bt.setBackgroundColor(getResources().getColor(R.color.colorverify));
                relation_tv.setText( profileemail+" sent you Request");
                option1_bt.setText("Accept Request");
                option2_bt.setBackgroundColor(getResources().getColor(R.color.colordisabled));
                option2_bt.setText("Ignore Request");
                option1_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addFriendToDb(user,profileemail);
                    }
                });
            }
        });
    }

    private void addFriendToDb(final String user, final String profileemail) {
        HashMap<String,Object> userData=new HashMap<>();

        userData.put("status","friends");

        FirebaseFirestore.getInstance().collection("requests").document(doc)
                .set(userData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                option1_bt.setVisibility(View.VISIBLE);
                option2_bt.setVisibility(View.INVISIBLE);
                option1_bt.setBackgroundColor(getResources().getColor(R.color.colorerror));
                relation_tv.setText("Already Friends");
                option1_bt.setText("Unfriend" );
                option1_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unfriendFromDb(user,profileemail);
                    }
                });

                addchatdocument(user,profileemail);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                option2_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                option1_bt.setVisibility(View.VISIBLE);
                option2_bt.setVisibility(View.VISIBLE);
                option1_bt.setBackgroundColor(getResources().getColor(R.color.colorverify));
                relation_tv.setText(profileemail+" sent you Request");
                option1_bt.setText("Accept Request");
                option2_bt.setBackgroundColor(getResources().getColor(R.color.colordisabled));
                option2_bt.setText("Ignore Request");
                option1_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addFriendToDb(user,profileemail);
                    }
                });
            }
        });
    }

    private void addchatdocument(String user, String profileemail) {
        HashMap<String,Object> userData=new HashMap<>();

        userData.put("status","friends");

        HashMap<String,Object> usermap=new HashMap<>();
        usermap.put(user,new Timestamp( System.currentTimeMillis()).toString());
        usermap.put(profileemail,"");
        FirebaseFirestore.getInstance().collection("chats").document(doc)
                .set(usermap);
        FirebaseFirestore.getInstance().collection("chats").document(doc)
                .collection("messages").document("0").set(userData);

    }

    private void unfriendFromDb(final String user, final String profileemail) {


        FirebaseFirestore.getInstance().collection("requests")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(final QueryDocumentSnapshot document:task.getResult()){

                        if(document.getId().contains(user)&&document.getId().contains(profileemail)){

                            FirebaseFirestore.getInstance().collection("requests").document(doc).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                            option1_bt.setBackgroundColor(getResources().getColor(R.color.coloroption));
                                            option1_bt.setVisibility(View.VISIBLE);
                                            option2_bt.setVisibility(View.INVISIBLE);
                                            relation_tv.setText("Not Friends");
                                            option1_bt.setText("Send Friend Request");
                                            option1_bt.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    sendRequestToDb(user,profileemail);
                                                }
                                            });
                                            removechat();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                    option1_bt.setVisibility(View.VISIBLE);
                                    option2_bt.setVisibility(View.INVISIBLE);
                                    option1_bt.setBackgroundColor(getResources().getColor(R.color.colorerror));
                                    relation_tv.setText("Already Friends");
                                    option1_bt.setText("Unfriend" );
                                    option1_bt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            unfriendFromDb(user,profileemail);

                                        }
                                    });
                                    removechat();
                                }

                            });
                        }
                    }

                }
                option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                option1_bt.setVisibility(View.VISIBLE);
                option2_bt.setVisibility(View.INVISIBLE);
                option1_bt.setBackgroundColor(getResources().getColor(R.color.colorerror));
                option1_bt.setText("Unfriend" );
                option1_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unfriendFromDb(user,profileemail);
                    }
                });
            }
        });



        }

    private void removechat() {
        FirebaseFirestore.getInstance().collection("chats").document(doc).delete();

    }

    private void getchatdocument(final String id, final String profile_id) {
        FirebaseFirestore.getInstance().collection("requests").
        addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(QueryDocumentSnapshot documentSnapshot:value){
                    if(documentSnapshot.getId().contains(id)&&documentSnapshot.getId().contains(profile_id)) {
                        doc=documentSnapshot.getId();
                    }
                }
            }
        } );



    }


    private void sendRequestToDb(final String user, final String profileemail) {


        HashMap<String,Object> userData=new HashMap<>();
        userData.put("from",user);
        userData.put("to",profileemail);
        userData.put("status","pending");

        FirebaseFirestore.getInstance().collection("requests").document(user+profileemail)
                .set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                option1_bt.setTextColor(getResources().getColor(R.color.colorAccent));
                option1_bt.setVisibility(View.VISIBLE);
                option2_bt.setVisibility(View.INVISIBLE);
                option1_bt.setBackgroundColor(getResources().getColor(R.color.colordisabled));
                relation_tv.setText("you Sent Request");
                option1_bt.setText("Cancel Request");
                option1_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelRequestFromDb(user,profileemail);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                option1_bt.setBackgroundColor(getResources().getColor(R.color.coloroption));
                option1_bt.setVisibility(View.VISIBLE);
                option2_bt.setVisibility(View.INVISIBLE);
                relation_tv.setText("Not Friends");
                option1_bt.setText("Send Friend Request");
                option1_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendRequestToDb(user,profileemail);
                    }
                });
            }
        });

    }

    private void cancelRequestFromDb(final String user, final String profileemail) {
        FirebaseFirestore.getInstance().collection("requests").document(user+profileemail).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        option1_bt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        option1_bt.setBackgroundColor(getResources().getColor(R.color.coloroption));
                        option1_bt.setVisibility(View.VISIBLE);
                        option2_bt.setVisibility(View.INVISIBLE);
                        relation_tv.setText("Not Friends");
                        option1_bt.setText("Send Friend Request");
                        option1_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendRequestToDb(user,profileemail);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                option1_bt.setTextColor(getResources().getColor(R.color.colorAccent));
                option1_bt.setVisibility(View.VISIBLE);
                option2_bt.setVisibility(View.INVISIBLE);
                option1_bt.setBackgroundColor(getResources().getColor(R.color.colordisabled));
                option1_bt.setText("you sent Request");
                option1_bt.setText("Cancel Request");
                option1_bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelRequestFromDb(user,profileemail);
                    }
                });
            }
        });
    }





}