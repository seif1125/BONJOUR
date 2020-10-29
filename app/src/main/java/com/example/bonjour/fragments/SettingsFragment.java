package com.example.bonjour.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bonjour.AsyncTasks.deleteUser;
import com.example.bonjour.AsyncTasks.retreivesingleuser;
import com.example.bonjour.AsyncTasks.updateUser;
import com.example.bonjour.Classes.RoomFactory;
import com.example.bonjour.Entity.User;
import com.example.bonjour.R;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


import static com.example.bonjour.R.drawable.default_user;


public class SettingsFragment extends Fragment {

EditText username_et,status_et;
TextView remaincharacters_tv,remainstatuscharacters_tv;
ImageButton closename_ib,applyname_ib,editname_ib,closestatus_ib,applystatus_ib,editstatus_ib;
ConstraintLayout remove_cl,choose_cl;
String currentusername,currentstatus,imageUrl;
ProgressDialog progressDialog;
 public static final int GET_FROM_GALLERY = 3;



de.hdodenhof.circleimageview.CircleImageView circleImageView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_settings, container, false);


        setallViews(view);
        getUserData();
        setAllListeners(view);


        return view;
    }

    private void setAllListeners(View view) {

        remove_cl.setOnClickListener(new View.OnClickListener() {
            final String U=FirebaseAuth.getInstance().getCurrentUser().getEmail();
            @Override
            public void onClick(View view) {
                popupRemoveConfirmationDialogues();
            }
        });

        choose_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });



        closename_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username_et.setText(currentusername);
                blockUsernameEdit();

            }
        });

        applyname_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyUsernameInDb(username_et.getText().toString());
                blockUsernameEdit();
            }
        });

        editname_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowUsernameEdit();

            }
        });

        username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(username_et.getText().toString().isEmpty()){
                    applyname_ib.setImageResource(R.drawable.ic_baseline_donedis_24);
                    applyname_ib.setClickable(false);
                }
                else{
                    applyname_ib.setImageResource(R.drawable.ic_baseline_done_24);
                    applyname_ib.setClickable(true);
                }

                remaincharacters_tv.setText(String.valueOf(25-username_et.getText().toString().length()));

            }
        });


        closestatus_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status_et.setText(currentstatus);
                blockUserstatusEdit();
            }
        });

        applystatus_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyStatusInDb(status_et.getText().toString());
                blockUserstatusEdit();
            }
        });

        editstatus_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowUserstatusEdit();
            }
        });

        status_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(status_et.getText().toString().isEmpty()){
                    applystatus_ib.setClickable(false);
                }
                remainstatuscharacters_tv.setText(String.valueOf(50-status_et.getText().toString().length()
                ));
            }
        });
    }
    private void setallViews(View view) {
        circleImageView=view.findViewById(R.id.settings_profile_image);
        username_et=view.findViewById(R.id.settings_username_et);
        remaincharacters_tv=view.findViewById(R.id.settings_editname_character_remain_tv);
        closename_ib=view.findViewById(R.id.settings_closename_ib);
        applyname_ib=view.findViewById(R.id.settings_applyname_ib);
        editname_ib=view.findViewById(R.id.settings_editname_ib);
        remove_cl=view.findViewById(R.id.removephoto_cl);
        choose_cl=view.findViewById(R.id.changephoto_cl);
        status_et=view.findViewById(R.id.settings_status_et);
        remainstatuscharacters_tv=view.findViewById(R.id.settings_status_character_remain_tv);
        closestatus_ib=view.findViewById(R.id.settings_closestatus_ib);
        applystatus_ib=view.findViewById(R.id.settings_applystatus_ib);
        editstatus_ib=view.findViewById(R.id.settings_editstatus_ib);
        progressDialog=new ProgressDialog(getActivity());
    }


    private void modifyUsernameInDb(final String username) {

        progressDialog.setTitle("Updating Username...");
        progressDialog.setMessage("please wait few moments");
        progressDialog.setIcon(R.drawable.ic_baseline_update_24);
        progressDialog.show();
        HashMap<String,Object> userData=new HashMap<>();

        userData.put("username",username);
        final String U=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore.getInstance().collection("users").document(U)
                .set(userData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                 currentusername=username;
                 username_et.setText(currentusername);
                 progressDialog.dismiss();
                Toast.makeText(getActivity(),"Username is Updated To: "+username,Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                username_et.setText(currentusername);
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Failed to Update Username",Toast.LENGTH_LONG).show();
            }
        });

    }
    private void modifyStatusInDb(final String status) {
        progressDialog.setTitle("Updating Status...");
        progressDialog.setMessage("please wait few moments");
        progressDialog.setIcon(R.drawable.ic_baseline_update_24);
        progressDialog.show();
        HashMap<String,Object> userData=new HashMap<>();

        userData.put("status",status);
        final String U=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore.getInstance().collection("users").document(U)
                .set(userData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                currentstatus=status;
                status_et.setText(currentstatus);
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Status is Updated ",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                status_et.setText(currentstatus);
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Failed to Update status",Toast.LENGTH_LONG).show();
            }
        });

    }


    private void allowUsernameEdit(){
        editname_ib.setVisibility(View.INVISIBLE);
        username_et.setClickable(true);
        username_et.setFocusable(true);
        username_et.setFocusableInTouchMode(true);
        applyname_ib.setVisibility(View.VISIBLE);
        closename_ib.setVisibility(View.VISIBLE);
        remaincharacters_tv.setVisibility(View.VISIBLE);
        remaincharacters_tv.setText(String.valueOf(25-username_et.getText().toString().length()));
    }
    private  void blockUsernameEdit(){
        editname_ib.setVisibility(View.VISIBLE);
        username_et.setClickable(false);
        username_et.setFocusable(false);
        username_et.setFocusableInTouchMode(false);
        applyname_ib.setVisibility(View.INVISIBLE);
        closename_ib.setVisibility(View.INVISIBLE);
        remaincharacters_tv.setVisibility(View.INVISIBLE);
    }
    private void allowUserstatusEdit(){
        editstatus_ib.setVisibility(View.INVISIBLE);
        status_et.setClickable(true);
        status_et.setFocusable(true);
        status_et.setFocusableInTouchMode(true);
        applystatus_ib.setVisibility(View.VISIBLE);
        closestatus_ib.setVisibility(View.VISIBLE);
        remainstatuscharacters_tv.setVisibility(View.VISIBLE);
        remainstatuscharacters_tv.setText(String.valueOf(50-status_et.getText().toString().length()));
    }
    private void blockUserstatusEdit(){
        editstatus_ib.setVisibility(View.VISIBLE);
        status_et.setClickable(false);
        status_et.setFocusable(false);
        status_et.setFocusableInTouchMode(false);
        applystatus_ib.setVisibility(View.INVISIBLE);
        closestatus_ib.setVisibility(View.INVISIBLE);
        remainstatuscharacters_tv.setVisibility(View.INVISIBLE);
    }
    private void getUserData(){
        final String U=FirebaseAuth.getInstance().getCurrentUser().getEmail();

         DocumentReference documentReference= FirebaseFirestore.getInstance().collection("users").document(U);
         documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       username_et.setText(document.getData().get("username").toString());
                       status_et.setText(document.getData().get("status").toString());
                       currentusername=document.getData().get("username").toString();
                        currentstatus=document.getData().get("status").toString();

                       if(document.getData().get("Image").equals("default")) {
                           Picasso.get().load(default_user).into(circleImageView);
                       }
                       else{
                           Picasso.get().load(document.getData().get("Image").toString()).into(circleImageView);
                       }
                    } else {

                    }
                } else {

                }
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            changeUserImageInStorage(selectedImage);

        }
    }
    private void changeUserImageInStorage(Uri path) {
        progressDialog.setTitle("Updating Image...");
        progressDialog.setMessage("please wait few moments");
        progressDialog.setIcon(R.drawable.ic_baseline_update_24);
        progressDialog.show();
        final String U=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        final StorageReference filename = FirebaseStorage.getInstance().getReference().child("images/").child(U);
        filename.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl=uri.toString();
                        modifyImagePathInDB(uri.toString());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Failed to Update Image",Toast.LENGTH_LONG).show();

            }
        });

    }
    private void modifyImagePathInDB(final String pathstring) {


        HashMap<String,Object> userData=new HashMap<>();

        userData.put("Image",pathstring);
        final String U=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore.getInstance().collection("users").document(U)
                .set(userData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Picasso.get().load(pathstring).into(circleImageView);
                progressDialog.dismiss();
                updateAccountIfSaved(pathstring);

                Toast.makeText(getActivity(),"Profile Image is Updated ",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Failed to Update Image",Toast.LENGTH_LONG).show();
            }
        });



    }

    private void updateAccountIfSaved(String pathstring) {
        final String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        try {
            User u = new retreivesingleuser(RoomFactory.getDB(requireContext()).getDbDAO()).execute(user).get();
            if(u!=null){
                new updateUser(RoomFactory.getDB(requireContext()).getDbDAO()).execute(user,pathstring);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void modifyImagePathToDefaultInDB(){
        progressDialog.setTitle("Removing Image...");
        progressDialog.setMessage("please wait few moments");
        progressDialog.setIcon(R.drawable.ic_baseline_update_24);
        progressDialog.show();

        HashMap<String,Object> userData=new HashMap<>();

        userData.put("Image","default");

        final String U=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore.getInstance().collection("users").document(U)
                .set(userData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Picasso.get().load(default_user).into(circleImageView);
                progressDialog.dismiss();
                updateAccountIfSaved("default");
                Toast.makeText(getActivity(),"Photo is Removed",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Failed to Remove Photo",Toast.LENGTH_LONG).show();
            }
        });
    }
    private  void popupRemoveConfirmationDialogues(){

         new AlertDialog.Builder(getActivity())
                 .setTitle("Title")
                 .setMessage("Are you Sure you want to Remove Photo!")
                 .setIcon(android.R.drawable.ic_dialog_alert)

                 .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                     public void onClick(DialogInterface dialog, int whichButton) {

                         modifyImagePathToDefaultInDB();
                     }})
                 .setNegativeButton(android.R.string.no, null).show();

     }


}

