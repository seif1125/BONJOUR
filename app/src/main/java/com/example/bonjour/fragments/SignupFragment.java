package com.example.bonjour.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bonjour.AsyncTasks.insertUser;
import com.example.bonjour.AsyncTasks.retreivesingleuser;
import com.example.bonjour.Classes.RoomFactory;
import com.example.bonjour.Dashboard;
import com.example.bonjour.Entity.User;
import com.example.bonjour.IntroCarousel;
import com.example.bonjour.MainActivity;
import com.example.bonjour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;


public class SignupFragment extends Fragment {
    EditText username_et,useremail_et,password_et,confirmpassword_et;
    TextView imagepath_tv,tologin_tv,signuperrortext_tv;
    ProgressBar signup_pb;
    ImageView attach_iv;
    Button signup_bt;
    Uri image;
    String userImageUrl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_signup, container, false);

        username_et=view.findViewById(R.id.signup_username_et);
        useremail_et=view.findViewById(R.id.signup_useremail_et);
        password_et=view.findViewById(R.id.signup_userpassword_et);
        confirmpassword_et=view.findViewById(R.id.signup_conuserpassword);
        signuperrortext_tv=view.findViewById(R.id.signup_erroruser_tv);
        imagepath_tv=view.findViewById(R.id.imagepath_et);
        attach_iv=view.findViewById(R.id.attach_iv);
        tologin_tv=view.findViewById(R.id.login_tv);
        signup_pb=view.findViewById(R.id.signup_progress_pb);
        signup_bt=view.findViewById(R.id.signup_bt);


        attach_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileManager();
            }
        });

        signup_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup_pb.setVisibility(View.VISIBLE);
                if(isValid()){
                    signupFirebase();
                }
                else{
                    signup_pb.setVisibility(View.INVISIBLE);
                }
            }
        }
        );
        tologin_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new SavedUserLoginFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.my_nav_host_fragment, newFragment);
                transaction.commit();
            }
        });
        return  view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode== Activity.RESULT_OK){
                image=data.getData();
                imagepath_tv.setText(image.getLastPathSegment());
            }
        }
    }
    public void signupFirebase(){
        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(useremail_et.getText().toString(),password_et.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            uploadImageToFirebase();
                        }
                        else{
                            signup_pb.setVisibility(View.INVISIBLE);
                            signuperrortext_tv.setText(task.getException().getMessage());
                        }
                    }
                });
    }
    private void uploadImageToFirebase() {
        final StorageReference filename =FirebaseStorage.getInstance().getReference().child("images/").child(useremail_et.getText().toString());
        filename.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userImageUrl=uri.toString();
                        addDataToFirebaseDatabase(uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signuperrortext_tv.setText(e.getMessage());
            }
        });
    }
    private void addDataToFirebaseDatabase(String uri) {
        ArrayList<String> friendList=new ArrayList<>();
        ArrayList<String> blockList=new ArrayList<>();
        HashMap<String,Object> userData=new HashMap<>();
        userData.put("username",username_et.getText().toString());
        userData.put("status","active");
        userData.put("Image",uri);
        userData.put("online",false);
        FirebaseFirestore.getInstance().collection("users").document(useremail_et.getText().toString())
                .set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                askForSavingEmail();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signuperrortext_tv.setText(e.getMessage());
            }
        });


    }
    private void askForSavingEmail() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Save User Account")
                .setMessage("Do you want to save This Account")
                .setIcon(R.drawable.logo)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        InsertUserFromRoom();
                    }})
                .setNegativeButton("Cancel", null).show();
    }
    private void InsertUserFromRoom() {
        User u=new User(password_et.getText().toString(),userImageUrl,useremail_et.getText().toString());
        new insertUser(RoomFactory.getDB(requireContext()).getDbDAO()).execute(u);
        goToDashboardClass();
    }
    private void openFileManager() {
        Intent  intent= new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,1);
    }
    private boolean isValid() {
        if(useremail_et.getText().toString().isEmpty()||username_et.getText().toString().isEmpty()||password_et.getText().toString().isEmpty()||confirmpassword_et.getText().toString().isEmpty()){
            signuperrortext_tv.setText("empty fields are invalid");
            return false;
        }
        else if(!password_et.getText().toString().equals(confirmpassword_et.getText().toString())){
            signuperrortext_tv.setText("passwords does not match");
            return false;
        }
        else if (imagepath_tv.getText().toString().isEmpty()){
            signuperrortext_tv.setText("please insert an image");
            return false;
        }
        else {

            return  true;
        }
    }











    protected void goToDashboardClass() {

        Intent intent=new Intent(getActivity(), Dashboard.class);

        startActivity(intent);
        getActivity().finish();
    }

}