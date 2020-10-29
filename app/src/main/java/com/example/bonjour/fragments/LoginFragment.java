package com.example.bonjour.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bonjour.AsyncTasks.insertUser;
import com.example.bonjour.AsyncTasks.retreivesingleuser;
import com.example.bonjour.Classes.RoomFactory;
import com.example.bonjour.Dashboard;
import com.example.bonjour.Entity.User;
import com.example.bonjour.IntroCarousel;
import com.example.bonjour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class LoginFragment extends Fragment {
    TextView email_tv,pass_tv,errortext_tv,sign_tv;
    ProgressBar login_pb;
    Button login_bt;
    String userImageUrl;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserAuth();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_login, container, false);

        email_tv=view.findViewById(R.id.login_useremail_et);
        pass_tv=view.findViewById(R.id.login_userpassword_et);
        sign_tv=view.findViewById(R.id.register_tv);
        errortext_tv=view.findViewById(R.id.erroruser_tv);
        login_bt=view.findViewById(R.id.login_bt);





        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if (isValidLogin()){
                    loginFirebase();

                }

            }
        });


        sign_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new SignupFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.my_nav_host_fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return  view;
    }
    
    private void loginFirebase() {
        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email_tv.getText().toString(),pass_tv.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                        checkUserAuth(firebaseUser);
                        }
                        else{
                            errortext_tv.setText(task.getException().getMessage());
                        }
                    }
                });





    }
    private void checkUserAuth(FirebaseUser user) {
        if(user!=null){
            checkRoomHasThisEmail(user.getEmail());
        }
        else{
            goToDashboardClass();
        }
    }
    private void checkRoomHasThisEmail(String email) {
        try {
            User user = new retreivesingleuser(RoomFactory.getDB(requireContext()).getDbDAO()).execute(email).get();
            if(user==null){
                askForSavingEmail();
            }
            else{
                goToDashboardClass();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private void askForSavingEmail() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Save User Account")
                .setMessage("Do you want to save This Account")
                .setIcon(R.drawable.logo)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getImage(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToDashboardClass();
                    }
                }).show();
    }
    private void getImage(String email) {
        FirebaseFirestore.getInstance().collection("users")
                .document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userImageUrl=documentSnapshot.getData().get("Image").toString();
                InsertUserFromRoom();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userImageUrl="default";
            }
        });
    }
    private void InsertUserFromRoom() {
        User u=new User(pass_tv.getText().toString(),userImageUrl,email_tv.getText().toString());
        new insertUser(RoomFactory.getDB(requireContext()).getDbDAO()).execute(u);
        goToDashboardClass();
    }
    private boolean isValidLogin() {
        if(email_tv.getText().toString().isEmpty()||pass_tv.getText().toString().isEmpty()){
            errortext_tv.setText("empty fields are invalid");
            return false;
        }
        else{
            return  true;
        }
    }
    private void checkUserAuth() {


        if(FirebaseAuth.getInstance().getCurrentUser()!=null){

            goToDashboardClass();
        }
    }
    private void goToDashboardClass() {
        Intent intent=new Intent(getActivity(), Dashboard.class);
        startActivity(intent);
        getActivity().finish();
    }

}