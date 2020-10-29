package com.example.bonjour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class Dashboard extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ListenerRegistration emailslistener,messagelistener;
    final String U = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    ArrayList<String> emails = new ArrayList<>();







    @Override
    protected void onStart() {
        super.onStart();
        checkUserAuth();
    }





    private void checkUserAuth() {
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
         goToLogin();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setToolBar();

        stopShowingService();
        getEmail();
        setUserOnline(true);
        bottomNavigationView=findViewById(R.id.dashboard_bottom_nav_bn);
        NavController navController = Navigation.findNavController(this, R.id.my_nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
    private void setUserOnline(boolean b) {
        HashMap<String,Boolean> usermap=new HashMap<>();
        if(b==true){
            usermap.put("online",true);
        }
        else {
            usermap.put("online",false);
        }
        FirebaseFirestore.getInstance().collection("users").document(U)
        .set(usermap, SetOptions.merge());
    }
    private void stopShowingService() {
        Intent i = new Intent(Dashboard.this, notificationListener.class);
        stopService(i);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.dashboardmenu,menu);
         return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if(item.getItemId()==R.id.logout){
             stopShowingService();
             FirebaseAuth.getInstance().signOut();
             goToLogin();

         }
         return true;
    }
    private void goToLogin() {
        Intent intent=new Intent(Dashboard.this,IntroCarousel.class);
        startActivity(intent);
        finish();
    }
    private void setToolBar() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.dashboardtoolbar);
    }
    @Override
    protected void onStop() {
        super.onStop();
        setUserOnline(false);
    }
    private void startShowingService(int unreads) {
        Intent i = new Intent(Dashboard.this, notificationListener.class);
        i.putExtra("currentunread",unreads);
        startService(i);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setUserOnline(true);
    }
    private void getEmail() {

        emailslistener = FirebaseFirestore.getInstance().collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        emails.clear();

                        for (QueryDocumentSnapshot chatdocs : value) {


                            if (chatdocs.getId().contains(U)) {
                                emails.add(chatdocs.getId().replace(U, ""));
                                getMessages(chatdocs.getId());
                            }
                        }


                    }
                });
    }
    private void getMessages(final String doc) {




        messagelistener= FirebaseFirestore.getInstance().collection("chats")
                .document(doc).collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                        int unreadmsg=0;
                        for (QueryDocumentSnapshot messagesdetails : value) {

                            if (!messagesdetails.getId().equals("0")) {
                                if (!messagesdetails.getData().get("from").toString().equals(U) && Boolean.parseBoolean(messagesdetails.getData().get("read").toString()) != true) {
                                    unreadmsg = unreadmsg + 1;

                                    startShowingService(unreadmsg);
                                }
                            }
                        }
                    }


                });



    }


}



