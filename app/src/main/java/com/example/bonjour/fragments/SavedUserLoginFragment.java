package com.example.bonjour.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bonjour.AsyncTasks.deleteUser;
import com.example.bonjour.AsyncTasks.getUser;
import com.example.bonjour.AsyncTasks.insertUser;
import com.example.bonjour.AsyncTasks.retreivesingleuser;
import com.example.bonjour.Classes.RoomFactory;
import com.example.bonjour.Dashboard;
import com.example.bonjour.Entity.User;
import com.example.bonjour.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;


public class SavedUserLoginFragment extends Fragment {
    Spinner spin;
    ArrayList<String> userList=new ArrayList<>();
    String password,imageurl,email;
    CircleImageView profile_image;
    TextView remove,logother,goregister;
    View v;
    Button login;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_saved_user_login, container, false);
        checkUserAuth();
        profile_image=view.findViewById(R.id.circleImageView);
        remove=view.findViewById(R.id.removeacc_tv);
        login=view.findViewById(R.id.saveduserlogin_bt);
        logother=view.findViewById(R.id.loginother_tv);
        goregister=view.findViewById(R.id.gotoregister_tv);
        spin = view.findViewById(R.id.spinner1);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                retreiveUserData(userList.get(i));
            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Removing User")
                        .setMessage("Do you really want to Remove User")
                        .setIcon(R.drawable.logo)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteUserFromRoom();
                            }})
                        .setNegativeButton("Cancel", null).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFirebase();
            }
        });

        logother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment newFragment = new LoginFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.my_nav_host_fragment, newFragment);
                transaction.commit();
            }
        });

        goregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new SignupFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.my_nav_host_fragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        getUsersEmailsFromRoom();
        return view;
    }

    private void deleteUserFromRoom() {
        User deletuser=new User(password,imageurl,email);
        new deleteUser(RoomFactory.getDB(getActivity()).getDbDAO()).execute(deletuser);
        getUsersEmailsFromRoom();

    }
    private void getUsersEmailsFromRoom() {
        ArrayList<User> users = new ArrayList<User>();
        users.clear();
        try{
            users.addAll( new getUser( RoomFactory.getDB(getActivity()).getDbDAO()).execute().get());
        }
       catch (ExecutionException e) {
            e.printStackTrace();
        }
      catch (InterruptedException e) {
            e.printStackTrace();
        }
    addEmailsToList(users);
    }
    private void addEmailsToList(ArrayList<User> users) {
        userList.clear();
        for(int i=0;i<users.size();i++){
            userList.add(users.get(i).getEmail());
        }
        updateuserList();

        if(users.isEmpty()){
            Fragment newFragment = new LoginFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.my_nav_host_fragment, newFragment);
            transaction.commit();
        }
    }
    private void updateuserList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, userList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
    }
    private void retreiveUserData(String user) {
        try{
            User retreiveduser=new retreivesingleuser(RoomFactory.getDB(getActivity()).getDbDAO()).execute(user).get();
            password=retreiveduser.getPassword();
            email=retreiveduser.getEmail();
            imageurl=retreiveduser.getUserimage();
            Picasso.get().load(imageurl).placeholder(R.drawable.default_user).into(profile_image);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void loginFirebase() {
        FirebaseAuth mAuth;
        mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password)
        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                     checkUserAuth();
                }
            }
        });
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