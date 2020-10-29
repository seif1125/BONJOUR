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
import android.widget.Button;
import android.widget.TextView;

import com.example.bonjour.R;
import com.example.bonjour.UserProfile;
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

import static com.google.firebase.auth.FirebaseAuth.getInstance;


public class RequestsFragment extends Fragment {


    ArrayList<String> username=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> images=new ArrayList<>();
    ArrayList<String> emails=new ArrayList<>();
    ArrayList<String> docs=new ArrayList<>();
    ListenerRegistration requestlistener,userlistener;


    RecyclerView rv;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_requests, container, false);
        rv=view.findViewById(R.id.request_recycler_rv);
        final String  U = getInstance().getCurrentUser().getEmail();
        requestlistener=FirebaseFirestore.getInstance().collection("requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        emails.clear();
                        username.clear();
                        images.clear();
                        status.clear();
                        docs.clear();

                        for(QueryDocumentSnapshot document:value){



                            if(document.getId().contains(U)){
                                String documentstatus = document.getData().get("status").toString();


                                if(documentstatus.equals("pending")){

                                    if( document.getData().get("to").toString().equals(U)){

                                        emails.add(document.getData().get("from").toString());
                                        docs.add(document.getId());

                                    }

                                }
                            }

                        }
                        FirebaseFirestore.getInstance().collection("users")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {



                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                        for(QueryDocumentSnapshot document:value){

                                            for(int i=0;i<emails.size();i++) {


                                                if (document.getId().contains(emails.get(i))) {
                                                    username.add(document.getData().get("username").toString());
                                                    status.add(document.getData().get("status").toString());
                                                    images.add(document.getData().get("Image").toString());

                                                }
                                            }
                                        }

                                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        RequestsFragment.UserAdapter userAdapterList=new RequestsFragment.UserAdapter();
                                        userAdapterList.notifyDataSetChanged();
                                        rv.setAdapter(userAdapterList);

                                    }

                                });

                    }
                });

        userlistener=FirebaseFirestore.getInstance().collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {



                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        username.clear();
                        images.clear();
                        status.clear();


                        for(QueryDocumentSnapshot document:value){

                            for(int i=0;i<emails.size();i++) {


                                if (document.getId().contains(emails.get(i))) {

                                    username.add(document.getData().get("username").toString());
                                    status.add(document.getData().get("status").toString());
                                    images.add(document.getData().get("Image").toString());

                                }
                            }
                        }

                        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        RequestsFragment.UserAdapter userAdapterList=new RequestsFragment.UserAdapter();
                        userAdapterList.notifyDataSetChanged();
                        rv.setAdapter(userAdapterList);

                    }

                });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        userlistener.remove();
        requestlistener.remove();
    }

    public class  UserAdapter extends RecyclerView.Adapter<RequestsFragment.UserAdapter.MyViewHolder>{



        @NonNull
        @Override
        public RequestsFragment.UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            View v=inflater.inflate(R.layout.requestitem, parent, false);
            final RequestsFragment.UserAdapter.MyViewHolder holder = new RequestsFragment.UserAdapter.MyViewHolder(v);

            return holder;

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            final String  U = getInstance().getCurrentUser().getEmail();
            holder.usernameitem.setText(username.get(position));

            Picasso.get().load(images.get(position)).into(holder.profileitem);

           holder.accept.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   HashMap<String,Object> friendData=new HashMap<>();

                   friendData.put("status","friends");

                   HashMap<String,Object> usertimes=new HashMap<>();
                   usertimes.put(U,new Timestamp( System.currentTimeMillis()).toString());
                   usertimes.put(emails.get(position),"");

                   FirebaseFirestore.getInstance().collection("requests").document(docs.get(position))
                           .set(friendData, SetOptions.merge());
                   FirebaseFirestore.getInstance().collection("chats").document(docs.get(position))
                           .set(usertimes);
                   FirebaseFirestore.getInstance().collection("chats").document(docs.get(position))
                   .collection("messages").document("0").set(friendData);
               }
           });
            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FirebaseFirestore.getInstance().collection("requests").document(emails.get(position)+U)
                            .delete();
                }
            });

            holder.viewprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(view.getContext(),UserProfile.class);
                    intent.putExtra("profile_id",emails.get(position));
                    intent.putExtra("profilename",username.get(position));
                    intent.putExtra("profileimage",images.get(position));
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
            TextView usernameitem;
            Button accept,decline,viewprofile;
            CircleImageView profileitem;
            public MyViewHolder(@NonNull final View itemView) {
                super(itemView);
                usernameitem=itemView.findViewById(R.id.request_username_tv);
                profileitem=itemView.findViewById(R.id.request_profile_iv);
                accept=itemView.findViewById(R.id.request_accept_bt);
                viewprofile=itemView.findViewById(R.id.request_view_bt);
                decline=itemView.findViewById(R.id.request_decline_bt);
            }
        }
    }



}