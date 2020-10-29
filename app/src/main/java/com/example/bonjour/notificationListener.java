package com.example.bonjour;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class notificationListener extends Service {
    final String U = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    ArrayList<String> emails = new ArrayList<>();
    ListenerRegistration emailslistener;
    ListenerRegistration messagelistener;

     static int newUnreadmessages;

    public notificationListener() {
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        newUnreadmessages=intent.getIntExtra("currentunread",0);
        System.out.println("main unreadd" + newUnreadmessages);
        getEmail();

        return START_REDELIVER_INTENT;
    }

    private void getEmail() {
        System.out.println("main username" + U);
        emailslistener = FirebaseFirestore.getInstance().collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        System.out.println("Search for chats started");
                        emails.clear();

                        for (QueryDocumentSnapshot chatdocs : value) {
                            System.out.println("main document" + chatdocs.getId());

                            if (chatdocs.getId().contains(U)) {
                                emails.add(chatdocs.getId().replace(U, ""));

                                System.out.println(chatdocs.getId().replace(U, "") + "isadded");
                                getMessages(chatdocs.getId());
                            }
                        }


                    }
                });
    }
    private void getMessages(final String doc) {

        System.out.println("usernameuuu" + U+emails);


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
                                    System.out.println("xxx adding unread" + unreadmsg);
                                }

                            }

                            if (unreadmsg > newUnreadmessages) {

                            newUnreadmessages = unreadmsg;
                            notificationDialog(doc.replace(U, ""), unreadmsg);
                        }

                        }
                    }


                });



    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(emailslistener!=null&&messagelistener!=null){
        emailslistener.remove();
        messagelistener.remove();
        }
        System.out.println("serrvice stopped");
    }
    private void notificationDialog(String sender,int unread) {
        NotificationManager notificationManager = (NotificationManager)       getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "Bonjour";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "new messages", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("you got a new message");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.colorPrimary);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.logo)
                .setTicker("Bonjour")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("new message")
                .setContentText("you have unread msg")
                .setContentInfo("Information");
        notificationManager.notify(1, notificationBuilder.build());}

}
