package com.example.bonjour.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bonjour.ChatsActivity;
import com.example.bonjour.R;
import com.example.bonjour.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static com.google.firebase.auth.FirebaseAuth.getInstance;


public class FriendsFragment extends Fragment {

    final String  U = getInstance().getCurrentUser().getEmail();
    ArrayList<String> username=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> images=new ArrayList<>();
    ArrayList<String> emails=new ArrayList<>();
    ArrayList<Integer> onlineindex=new ArrayList<>();
    ArrayList<Integer> offlineindex=new ArrayList<>();
    ArrayList<Integer> sortedindex=new ArrayList<>();
    RecyclerView rv;
    ListenerRegistration friendsemaillistener,friendsdatalistener;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_friends, container, false);
        rv=view.findViewById(R.id.friends_recyclerview_rv);
        listenRequestsEmails();
        listenToFriendsData();
        return view;
    }

    private void listenRequestsEmails() {
        friendsemaillistener= FirebaseFirestore.getInstance().collection("requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        emails.clear();
                        username.clear();
                        images.clear();
                        status.clear();
                        onlineindex.clear();
                        offlineindex.clear();
                        sortedindex.clear();
                        for(QueryDocumentSnapshot document:value){
                            if(document.getId().contains(U)){
                                String documentstatus = document.getData().get("status").toString();
                                if(documentstatus.equals("friends")){
                                    if( document.getData().get("to").toString().equals(U)){
                                        emails.add(document.getData().get("from").toString());

                                    }
                                    else{
                                        emails.add(document.getData().get("to").toString());

                                    }
                                }
                            }
                        }

                    }
                });
        listenToFriendsData();
    }
    private void listenToFriendsData() {
       friendsdatalistener=FirebaseFirestore.getInstance().collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {



                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for(QueryDocumentSnapshot document:value){
                            for(int i=0;i<emails.size();i++) {

                                if (document.getId().contains(emails.get(i))) {

                                    username.add(document.getData().get("username").toString());
                                    status.add(document.getData().get("status").toString());
                                    images.add(document.getData().get("Image").toString());
                                    if((Boolean) document.getData().get("online")){
                                        onlineindex.add(i);
                                    }
                                    else{
                                        offlineindex.add(i);
                                    }
                                }
                            }
                        }
                        for (int i=0;i<onlineindex.size();i++){
                            sortedindex.add(onlineindex.get(i));
                        }
                        for(int i=0;i<offlineindex.size();i++){
                            sortedindex.add(offlineindex.get(i));
                        }
                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        FriendsFragment.UserAdapter userAdapterList=new FriendsFragment.UserAdapter();
                        userAdapterList.notifyDataSetChanged();
                        rv.setAdapter(userAdapterList);
                    }

                });
    }
    @Override
    public void onStop() {
        super.onStop();
        if(friendsdatalistener!=null&&friendsemaillistener!=null){
        friendsemaillistener.remove();
        friendsdatalistener.remove();
        }
    }
    public class  UserAdapter extends RecyclerView.Adapter<FriendsFragment.UserAdapter.MyViewHolder>{



        @NonNull
        @Override
        public FriendsFragment.UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            View v=inflater.inflate(R.layout.frienditem, parent, false);
            final FriendsFragment.UserAdapter.MyViewHolder holder = new FriendsFragment.UserAdapter.MyViewHolder(v);

            return holder;

        }

        @Override
        public void onBindViewHolder(@NonNull final FriendsFragment.UserAdapter.MyViewHolder holder, final int position) {
            holder.usernameitem.setText(username.get(sortedindex.get(position)));
            holder.statusitem.setText(status.get(sortedindex.get(position)));
            Picasso.get().load(images.get(sortedindex.get(position))).placeholder(R.drawable.default_user).into(holder.profileitem);

            if(position>=0&&position<onlineindex.size()){
                holder.onlineitem.setImageResource(R.drawable.ic_baseline_online_24);
            }
            else{
                holder.onlineitem.setImageResource(R.drawable.ic_baseline_adjust_24);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getActivity())
                    .setTitle(holder.usernameitem.getText().toString())
                            .setMessage("choose action")
                            .setIcon(R.drawable.logo)
                            .setPositiveButton("View Profile", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent=new Intent(getContext(), UserProfile.class);
                                    intent.putExtra("profile_id",emails.get(sortedindex.get(position)));
                                    intent.putExtra("profilename",username.get(sortedindex.get(position)));
                                    intent.putExtra("profileimage",images.get(sortedindex.get(position)));
                                    intent.putExtra("profilestatus",status.get(sortedindex.get(position)));
                                    getActivity().startActivity(intent);

                                }})
                            .setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getContext(), ChatsActivity.class);
                                    intent.putExtra("profile_id", emails.get(sortedindex.get(position)));
                                    intent.putExtra("profilename", username.get(sortedindex.get(position)));
                                    intent.putExtra("profileimage", images.get(sortedindex.get(position)));
                                    intent.putExtra("profilestatus",status.get(sortedindex.get(position)));

                                    getActivity().startActivity(intent);
                                }
                            }).show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return emails.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView usernameitem,statusitem;
            ImageView onlineitem;
            CircleImageView profileitem;
            public MyViewHolder(@NonNull final View itemView) {
                super(itemView);
                usernameitem=itemView.findViewById(R.id.username_row_name);
                statusitem=itemView.findViewById(R.id.userstatus_row_status);
                profileitem=itemView.findViewById(R.id.friend_row_profile);
                onlineitem=itemView.findViewById(R.id.online_iv);
            }
        }
    }



}