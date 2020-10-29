package com.example.bonjour;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    Button start_bt;
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        start_bt=findViewById(R.id.start_bt);
        pref = getSharedPreferences("user_visit",MODE_PRIVATE);

        checkFirstVisit();
        start_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("firsttime",false);
                editor.commit();
                goToLoginClass();
            }
        });





    }
    private void checkFirstVisit() {
        if(pref.contains("firsttime")){
            goToLoginClass();
        }

    }
    protected void goToLoginClass() {
        Intent intent=new Intent(MainActivity.this,IntroCarousel.class);
        startActivity(intent);
        finish();
    }


}