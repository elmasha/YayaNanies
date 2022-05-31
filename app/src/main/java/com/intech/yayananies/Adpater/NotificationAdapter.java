package com.intech.yayananies.Adpater;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.intech.yayananies.Models.Notification;
import com.intech.yayananies.R;
import com.intech.yayananies.TimeAgo;


import java.util.Date;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.ProviderViewHolder> {

    private OnItemCickListener listener;
    public Context context;


    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProviderViewHolder holder, int position, @NonNull Notification model) {

        if (model.getTitle() != null | model.getDesc() != null |model.getTimestamp() != null  | model.getStatus() != null ){
            holder.title.setText(model.getTitle());
            holder.desc.setText(model.getDesc());
            holder.time.setText(TimeAgo.getTimeAgo(model.getTimestamp().getTime()));
            long stamp = model.getTimestamp().getTime();


            String state = model.getStatus();

            if (state.equals("none")){
                holder.title.setTextColor(Color.parseColor("#0BF4DE"));
                holder.time.setTextColor(Color.parseColor("#0BF4DE"));
            }else {
                holder.title.setTextColor(Color.parseColor("#ffffff"));
                holder.time.setTextColor(Color.parseColor("#ffffff"));
            }

        } Date  now = new Date();
        if ( getDifferenceDays(now,model.getTimestamp()) > 29){
            deleteItem(position);
        }
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row,parent,false);
        this.context = parent.getContext();
        return new ProviderViewHolder(v);
    }

    public int getDifferenceDays(Date d1, Date d2) {
        int daysdiff = 0;
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return Math.abs(daysdiff);
    }

    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder{
       private TextView title, desc, time;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notify_title);
            desc = itemView.findViewById(R.id.notify_description);
            time = itemView.findViewById(R.id.notify_time);

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
