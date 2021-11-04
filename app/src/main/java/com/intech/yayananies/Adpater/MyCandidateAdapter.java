package com.intech.yayananies.Adpater;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyCandidateAdapter extends FirestoreRecyclerAdapter<Candidates, MyCandidateAdapter.CandidateViewHolder> {

    private OnItemCickListener listener;
    public List<Candidates> candidatesList;
    public Context context;


    public MyCandidateAdapter(@NonNull FirestoreRecyclerOptions<Candidates> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CandidateViewHolder holder, int position, @NonNull Candidates model) {
        holder.Name.setText(model.getCandidate_name());
        holder.county.setText(model.getCounty());
        holder.ward.setText(model.getWard());
        holder.mobile.setText(model.getMobile_no());
        holder.age.setText(model.getAge()+" yrs");
        holder.dob.setText(model.getDOB());
        holder.gender.setText(model.getGender());
        holder.experience.setText(model.getExperience());



        if(context != null | model.getProfile_image() != null) {
            Picasso.with(context).load(model.getProfile_image()).placeholder(R.drawable.load).error(R.drawable.errorimage).into(holder.profile);
        }




    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_candidate_row,parent,false);
        this.context = parent.getContext();
        return new CandidateViewHolder(v);
    }


    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class CandidateViewHolder extends RecyclerView.ViewHolder{
       private TextView Name, county, ward,mobile,age,meet,dob,experience,gender;
       private CircleImageView profile;
       private View view;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.row_name);

            county = itemView.findViewById(R.id.row_county);
            ward = itemView.findViewById(R.id.row_ward);
            mobile = itemView.findViewById(R.id.row_mobile);
            profile = itemView.findViewById(R.id.row_image);
            age = itemView.findViewById(R.id.row_age);
            dob = itemView.findViewById(R.id.row_dob);
            experience = itemView.findViewById(R.id.row_experience);
            gender = itemView.findViewById(R.id.row_gender);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);


                    }
                }
            });



        }
    }


    public interface  OnItemCickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemCickListener listener){

        this.listener = listener;

    }




}
