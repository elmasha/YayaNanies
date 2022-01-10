package com.intech.yayananies.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.intech.yayananies.Adpater.NotificationAdapter;
import com.intech.yayananies.Models.Notification;
import com.intech.yayananies.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotificationFragment extends Fragment {
View root;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference YayaEMPRef = db.collection("Yaya_Employer");

    private CircleImageView profileImage;

    private LinearLayout imageView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private NotificationAdapter adapter;
    private RecyclerView mRecyclerView;



    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        FetchNotification();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_notification, container, false);
        mAuth = FirebaseAuth.getInstance();
        swipeRefreshLayout = root.findViewById(R.id.swipeNotification);
        mRecyclerView = root.findViewById(R.id.recycler_notification);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchNotification();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });

        FetchNotification();

        return root;
    }


    private void FetchNotification() {
        Query query =  YayaEMPRef.document(mAuth.getCurrentUser().getUid())
                .collection("Notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);
        FirestoreRecyclerOptions<Notification> transaction = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new NotificationAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(LayoutManager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NotificationAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

            }
        });

    }

}