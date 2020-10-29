package com.example.bonjour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsActivity extends AppCompatActivity {
    ArrayList<String> messages= new ArrayList<>();
    ArrayList<String> chatdates = new ArrayList<>();
    ArrayList<String> chattimes = new ArrayList<>();
    ArrayList<Boolean> read = new ArrayList<>();
    ArrayList<Boolean> sender = new ArrayList<>();
    ArrayList<String> messagedocs = new ArrayList<>();
    ListenerRegistration messageslistener,userlistener;

    RecyclerView rv;
    ImageView send_iv,online_iv;
    EditText messageholder;
    TextView datetext;
    String parteneremail,document;
    CircleImageView profileimg;
    TextView profilename,lastseen;
    String useremail=FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        setinitialToolBar();
        System.out.println("user"+useremail);
        setUserOnline(true);
         rv=findViewById(R.id.chatsrecycler_rv);
         datetext=findViewById(R.id.datetexthoolder_tv);
        messageholder=findViewById(R.id.messageholder_et);
         send_iv=findViewById(R.id.send_iv);
         send_iv.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 sendmessage();
             }
         });
         getChatDocument(useremail,parteneremail);

    }

    private void sendmessage() {

        String message=messageholder.getText().toString().trim();
        HashMap<String,Object> messagemap=new HashMap<String,Object>();
        messagemap.put("from",useremail);
        messagemap.put("to",parteneremail);
        messagemap.put("read",false);
        messagemap.put("content",message);


        if(!message.isEmpty()){


            FirebaseFirestore.getInstance().collection("chats").document(document).collection("messages")
                    .document(new Timestamp(System.currentTimeMillis()).toString()).set(messagemap);
            messageholder.setText("");


        }



    }

    private void makeaListenerFormessages(final String document) {


        messageslistener=FirebaseFirestore.getInstance().collection("chats").document(document).collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                   messages.clear();
                   chatdates.clear();
                   chattimes.clear();
                   read.clear();
                   messagedocs.clear();
                   sender.clear();

                        for (QueryDocumentSnapshot queryDocumentSnapshot:value){
                            if(!queryDocumentSnapshot.getId().equals("0")) {
                                chatdates.add(queryDocumentSnapshot.getId().substring(0, 11));
                                chattimes.add(queryDocumentSnapshot.getId().substring(10, 16));
                                messages.add(queryDocumentSnapshot.getData().get("content").toString());
                                read.add((Boolean) queryDocumentSnapshot.getData().get("read"));
                                if (queryDocumentSnapshot.getData().get("from").equals(useremail)) {

                                    sender.add(true);
                                } else {
                                    sender.add(false);

                                    messagedocs.add(queryDocumentSnapshot.getId());


                                }

                            }
                        }
                        System.out.println(chatdates);
                        System.out.println(chattimes);
                        System.out.println(messages);
                        System.out.println(read);
                        System.out.println(sender);
                        markMessageAsRead(messagedocs,useremail);



                    }
                });
    }

    private void markMessageAsRead(final ArrayList<String> documents, String id) {

        for(int i=0;i<documents.size();i++){

            HashMap<String,Boolean> messagemap=new HashMap<>();
            messagemap.put("read",true);
            final int finalI = i;
            FirebaseFirestore.getInstance().collection("chats").document(document).collection("messages")
                    .document(documents.get(i)).set(messagemap,SetOptions.merge());

        }
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ChatsActivity.UserAdapter userAdapterList=new ChatsActivity.UserAdapter();
        userAdapterList.notifyDataSetChanged();
        rv.setAdapter(userAdapterList);
        rv.scrollToPosition(messages.size()-1);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                System.out.println(newState);
                datetext.setText(chatdates.get(messages.size()-newState-1));
            }
        });

    }


    private void getChatDocument(final String useremail, final String parteneremail) {
        FirebaseFirestore.getInstance().collection("chats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                                for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                    if(documentSnapshot.getId().contains(useremail)&&documentSnapshot.getId().contains(parteneremail)){
                                        document=documentSnapshot.getId();
                                    }

                                }
                                if(document!=null){
                                    makeaListenerFormessages(document);
                                    getLastSeen();
                                    addLastSeen();
                                }
                                else{
                                    rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    ChatsActivity.UserAdapter userAdapterList=new ChatsActivity.UserAdapter();
                                    userAdapterList.notifyDataSetChanged();
                                    rv.setAdapter(userAdapterList);

                                }



                        }



                    }
                });

    }

    private void getLastSeen() {
        FirebaseFirestore.getInstance().collection("chats").document(document)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.getData().get(parteneremail).toString().substring(0,10).equals(new Timestamp( System.currentTimeMillis()).toString().substring(0,10))){
                            lastseen.setText("lastseen today "+value.getData().get(parteneremail).toString().substring(10,16));
                        }
                        else{
                        lastseen.setText("lastseen  :  "+value.getData().get(parteneremail).toString().substring(0,16));
                        }
                    }
                });
    }
    private void addLastSeen(){
        HashMap<String,String> lastseenmap=new HashMap<>();
        lastseenmap.put(useremail,new Timestamp(System.currentTimeMillis()).toString());
        FirebaseFirestore.getInstance().collection("chats").document(document)
                .set(lastseenmap,SetOptions.merge());
    }

    private void makeaListenerForPartner() {
       userlistener= FirebaseFirestore.getInstance().collection("users").document(parteneremail)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        profilename.setText(value.getData().get("username").toString());
                        Picasso.get().load(value.getData().get("Image").toString()).placeholder(R.drawable.default_user).into(profileimg);
                        if(value.getData().get("online").toString().equals(true)){
                            online_iv.setVisibility(View.VISIBLE);
                        }
                        else{
                            online_iv.setVisibility(View.INVISIBLE);
                        }


                    }
                });
    }

    private void setinitialToolBar() {
        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.chatlayout);
        View barView=getSupportActionBar().getCustomView();
       profileimg=barView.findViewById(R.id.chat_user_profile);
       profilename=barView.findViewById(R.id.chat_user_name);
       lastseen=barView.findViewById(R.id.lastseen_tv);
       online_iv=barView.findViewById(R.id.chat_online_iv);
                Intent intent = getIntent();
                parteneremail=intent.getStringExtra("profile_id");

                profilename.setText(intent.getStringExtra("profilename"));
               Picasso.get().load(intent.getStringExtra("profileimage")).placeholder(R.drawable.default_user).into(profileimg);
               makeaListenerForPartner();
    }
    private void setUserOnline(boolean b) {
        HashMap<String,Boolean> usermap=new HashMap<>();
        if(b==true){
            usermap.put("online",true);
        }
        else {
            usermap.put("online",false);
        }
        FirebaseFirestore.getInstance().collection("users").document(useremail)
                .set(usermap, SetOptions.merge());
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chatmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.dashmenu_logout){
            FirebaseAuth.getInstance().signOut();
            gotoLogin();
        }
        if(item.getItemId()==R.id.dashmenu_profile){
            viewprofile();
        }
        return true;
    }

    private void gotoLogin() {
        stopShowingService();
        Intent intent=new Intent(ChatsActivity.this,IntroCarousel.class);
        startActivity(intent);
        finish();
    }
    private void stopShowingService() {
        Intent i = new Intent(ChatsActivity.this, notificationListener.class);
        stopService(i);
    }
    private void viewprofile() {
        Intent intent=new Intent(ChatsActivity.this,UserProfile.class);
        intent.putExtra("profile_id",parteneremail);
        intent.putExtra("profilename",profilename.getText().toString());
        intent.putExtra("profileimage",intent.getStringExtra("profileimage"));
        intent.putExtra("profilestatus",intent.getStringExtra("profileimage"));
       startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        setUserOnline(false);
        if(messageslistener!=null&&userlistener!=null){
        messageslistener.remove();
        userlistener.remove();
        }

    }
    protected void onStop() {
        super.onStop();
        setUserOnline(false);
        messageslistener.remove();
        userlistener.remove();

    }

    public class  UserAdapter extends RecyclerView.Adapter<ChatsActivity.UserAdapter.MyViewHolder>{



        @NonNull
        @Override
        public ChatsActivity.UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getApplicationContext());
            View v=inflater.inflate(R.layout.messages_item_layout, parent, false);
            final ChatsActivity.UserAdapter.MyViewHolder holder = new ChatsActivity.UserAdapter.MyViewHolder(v);

            return holder;

        }

        @Override
        public void onBindViewHolder(@NonNull ChatsActivity.UserAdapter.MyViewHolder holder, final int position) {

            if(sender.get(position)==true){
                holder.recceiverbar.setVisibility(View.INVISIBLE);
                holder.senderbar.setVisibility(View.VISIBLE);
                if(read.get(position)==true){
                    holder.seenitem.setVisibility(View.VISIBLE);
                }
                else{
                    holder.seenitem.setVisibility(View.INVISIBLE);
                }
                holder.sendmsg.setText(messages.get(position));
                holder.sendmsgtime.setText(chattimes.get(position));
            }
            else{
                holder.recceiverbar.setVisibility(View.VISIBLE);
                holder.senderbar.setVisibility(View.INVISIBLE);
                holder.receivemsg.setText(messages.get(position));
                holder.receivemsgtime.setText(chattimes.get(position));
            }

        }

        @Override
        public int getItemCount() {
            return chatdates.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ConstraintLayout recceiverbar,senderbar;
            TextView receivemsg,sendmsg,sendmsgtime,receivemsgtime;
            ImageView seenitem;



            CircleImageView profileitem;
            public MyViewHolder(@NonNull final View itemView) {
                super(itemView);
                recceiverbar=itemView.findViewById(R.id.receiver_msg_cl);
                senderbar=itemView.findViewById(R.id.sender_msg_cl);
                receivemsg=itemView.findViewById(R.id.chat_rec_msg);
                sendmsg=itemView.findViewById(R.id.chat_sen_msg);
                receivemsgtime=itemView.findViewById(R.id.chat_rec_time);
                sendmsgtime=itemView.findViewById(R.id.chat_sen_time);
               seenitem=itemView.findViewById(R.id.chat_isread_iv);

            }
        }
    }



}
