package com.example.bonjour.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bonjour.ChatsActivity;
import com.example.bonjour.R;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.auth.FirebaseAuth.getInstance;


public class chatsFragment extends Fragment {


    ArrayList<String> username = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<String> tempemails = new ArrayList<>();
    ArrayList<String> docs = new ArrayList<>();
    ArrayList<String> tempdocs = new ArrayList<>();
    ArrayList<String> lastchattimeStamp = new ArrayList<>();
    ArrayList<String> lastchattext = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();
    ArrayList<Boolean> read = new ArrayList<>();


    ArrayList<Boolean> sender = new ArrayList<>();
    ArrayList<Boolean> online = new ArrayList<>();
    ListenerRegistration emailslistener,messageslistener,userdatalistener;
    final String U = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    RecyclerView rv;
     ArrayList<String> unreads=new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        rv = view.findViewById(R.id.chats_recycler_rv);

        getEmailsAndDocs();


        return view;
    }

    private void getEmailsAndDocs() {


       emailslistener= FirebaseFirestore.getInstance().collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        tempemails.clear();
                        tempdocs.clear();




                        for(QueryDocumentSnapshot chatsdoc:value){

                            if(chatsdoc.getId().contains(U)){
                                tempemails.add(chatsdoc.getId().replace(U,""));
                                tempdocs.add(chatsdoc.getId());
                            }

                        }

                        for(int i=0;i<tempemails.size();i++) {

                            final int finalI = i;
                            FirebaseFirestore.getInstance().collection("chats").document(tempdocs.get(i))
                                    .collection("messages")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            emails.clear();
                                            docs.clear();

                                            if(value.getDocuments().size()<=1){

                                            }
                                            else{
                                                docs.add(tempdocs.get(finalI));
                                                emails.add(tempemails.get(finalI));

                                            }

                                            getmessages();
                                        }
                                    });


                        }

                    }
                });


    }
    private void getmessages() {


        for(int i=0;i<docs.size();){

            final int finalI=i;

           messageslistener= FirebaseFirestore.getInstance().collection("chats").document(docs.get(i)).
                    collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                   if(lastchattimeStamp.size()<=finalI) {
                       lastchattimeStamp.add(finalI, value.getDocuments().get(value.getDocuments().size() - 1).getId());
                   }
                   else{
                       lastchattimeStamp.set(finalI, value.getDocuments().get(value.getDocuments().size() - 1).getId());
                   }
                   if(lastchattext.size()<=finalI){
                       lastchattext.add(finalI, value.getDocuments().get(value.getDocuments().size() - 1).getData().get("content").toString());
                   }
                    else{
                        lastchattext.set(finalI, value.getDocuments().get(value.getDocuments().size() - 1).getData().get("content").toString());
                    }
                    if(lastchattext.size()<=finalI){
                        lastchattext.add(finalI, value.getDocuments().get(value.getDocuments().size() - 1).getData().get("content").toString());
                    }
                    else{
                        lastchattext.set(finalI, value.getDocuments().get(value.getDocuments().size() - 1).getData().get("content").toString());
                    }
                    if(read.size()<=finalI){
                        read.add(finalI, (Boolean) value.getDocuments().get(value.getDocuments().size() - 1).getData().get("read"));
                    }
                    else{
                        read.set(finalI, (Boolean) value.getDocuments().get(value.getDocuments().size() - 1).getData().get("read"));
                    }
                    if(value.getDocuments().get(value.getDocuments().size() - 1).getData().get("from").equals(U)){

                        if(sender.size()<=finalI){
                            sender.add(finalI,true);
                        }
                        else{
                            sender.set(finalI,true);
                        }

                    }
                  else{

                        if(sender.size()<=finalI){
                            sender.add(finalI,false);
                        }
                        else{
                            sender.set(finalI,false);
                        }

                    }



                    getUserData();
                }
            });
i++;
        }
    }
    private void getUserData() {

        for(int i=0;i<docs.size();){

            final int finalI = i;
            userdatalistener=FirebaseFirestore.getInstance().collection("users").document(emails.get(i)).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                   if(username.size()<=finalI) {
                       username.add(finalI, value.getData().get("username").toString());
                   }
                   else{
                       username.set(finalI,value.getData().get("username").toString());
                   }
                   if(images.size()<=finalI){
                    images.add(finalI,value.getData().get("Image").toString());
                   }
                   else{
                    images.set(finalI,value.getData().get("Image").toString());
                   }
                   if(status.size()<=finalI){
                    status.add(finalI,value.getData().get("status").toString());
                   }
                   else{
                       status.set(finalI,value.getData().get("status").toString());
                   }
                   if(online.size()<=finalI) {
                       online.add(finalI, (Boolean) value.getData().get("online"));
                   }
                   else{
                       online.set(finalI, (Boolean) value.getData().get("online"));
                   }

                   if(finalI==emails.size()-1){rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    chatsFragment.UserAdapter userAdapterList=new chatsFragment.UserAdapter();
                    userAdapterList.notifyDataSetChanged();
                    rv.setAdapter(userAdapterList);
                   }



                }
            });


            i++;
        }


    }


    @Override
    public void onStop() {
        super.onStop();
        if(userdatalistener!=null&&messageslistener!=null&&emailslistener!=null){
            userdatalistener.remove();
            messageslistener.remove();
            emailslistener.remove();
        }

    }

    public class UserAdapter extends RecyclerView.Adapter<chatsFragment.UserAdapter.MyViewHolder> {


            @NonNull
            @Override
            public chatsFragment.UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View v = inflater.inflate(R.layout.chatitem, parent, false);
                final chatsFragment.UserAdapter.MyViewHolder holder = new chatsFragment.UserAdapter.MyViewHolder(v);

                return holder;

            }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

                holder.usernameitem.setText(username.get(position));

                if(sender.get(position).equals(true)){
                    holder.chatitem.setText("you : "+lastchattext.get(position));
                    holder.unread_msg_item.setVisibility(View.INVISIBLE);
                    holder.chatitem.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    holder.chatitem.setTextColor(getResources().getColor(R.color.colorAccent));

                }
                else{
                    holder.chatitem.setText(username.get(position)+" : "+lastchattext.get(position));
                    if(read.get(position).equals(false)){
                        getunreadMessages(position,holder);
                        holder.unread_msg_item.setVisibility(View.VISIBLE);
                        holder.chatitem.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        holder.chatitem.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                    else{
                        holder.unread_msg_item.setVisibility(View.INVISIBLE);
                        holder.chatitem.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        holder.chatitem.setTextColor(getResources().getColor(R.color.colorAccent));

                    }
                }
                if(online.get(position).equals(true)){
                    holder.onlineitem.setVisibility(View.VISIBLE);
                }
                else{
                    holder.onlineitem.setVisibility(View.INVISIBLE);
                }
                Picasso.get().load(images.get(position)).placeholder(R.drawable.default_user).into(holder.profileitem);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), ChatsActivity.class);
                        intent.putExtra("profile_id", emails.get(position));
                        intent.putExtra("profilename", username.get(position));
                        intent.putExtra("profileimage", images.get(position));
                        intent.putExtra("profilestatus",status.get(position));
                        getActivity().startActivity(intent);
                    }
                });
            }


            @Override
            public int getItemCount() {
                return emails.size();
            }

            public class MyViewHolder extends RecyclerView.ViewHolder {
                TextView usernameitem, chatitem, unread_msg_item;
                ImageView onlineitem;
                CircleImageView profileitem;

                public MyViewHolder(@NonNull final View itemView) {
                    super(itemView);
                    usernameitem = itemView.findViewById(R.id.chat_row_name);
                    chatitem = itemView.findViewById(R.id.chat_row_lastmsg);
                    unread_msg_item = itemView.findViewById(R.id.chat_newmessage_tv);
                    profileitem = itemView.findViewById(R.id.chat_row_profile);
                    onlineitem = itemView.findViewById(R.id.chat_online_iv);
                }
            }
        }
    private void getunreadMessages(int position, final UserAdapter.MyViewHolder holder){

        FirebaseFirestore.getInstance().collection("chats").document(docs.get(position))
                .collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){int unreadmessages=0;
                    for(QueryDocumentSnapshot messages:task.getResult()){

                        if(!messages.getData().get("from").equals(U)){

                            if(messages.getData().get("read").equals(false)){


                                unreadmessages=unreadmessages+1;

                              holder.unread_msg_item.setText(new Integer(unreadmessages-1).toString());
                            }
                        }
                    }


                }
            }
        });



    }

    }


