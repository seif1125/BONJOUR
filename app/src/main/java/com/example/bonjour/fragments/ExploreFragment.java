package com.example.bonjour.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bonjour.R;
import com.example.bonjour.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;
import static com.google.firebase.auth.FirebaseAuth.*;


public class ExploreFragment extends Fragment {

ArrayList<String> username=new ArrayList<>();
ArrayList<String> status=new ArrayList<>();
ArrayList<String> images=new ArrayList<>();
    ArrayList<String> emails=new ArrayList<>();
RecyclerView rv;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_explore, container, false);
        rv=view.findViewById(R.id.explore_recyclerview_rv);
         final String  U = getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("Image")!=null){
                                    if(!document.getId().equals(U)){
                                        emails.add(document.getId());
                                 images.add(document.getData().get("Image").toString());
                                 username.add(document.getData().get("username").toString());
                                 status.add(document.getData().get("status").toString());
                                    }
                                }
                            }
                            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                            UserAdapter userAdapterList=new UserAdapter();
                            rv.setAdapter(userAdapterList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });




        return view;
    }


public class  UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{



    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View v=inflater.inflate(R.layout.users_row_item, parent, false);
        final MyViewHolder holder = new MyViewHolder(v);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, final int position) {
        holder.row_username.setText(username.get(position));
        holder.row_userstatus.setText(status.get(position));
        Picasso.get().load(images.get(position)).placeholder(R.drawable.default_user).into(holder.profile_row_image);
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
        return username.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView row_username,row_userstatus;
        Button viewprofile;
        CircleImageView profile_row_image;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            row_username=itemView.findViewById(R.id.user_row_name);
            row_userstatus=itemView.findViewById(R.id.user_row_status);
            profile_row_image=itemView.findViewById(R.id.user_row_profile);
            viewprofile=itemView.findViewById(R.id.view_profile_bt);
        }
    }
}


}