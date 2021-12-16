package com.intech.yayananies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.intech.yayananies.Adpater.CandidateAdapter;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectionActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    private TextView textUser,textEmail,textBureauName,toPref,
            selectName,selectSalary,selectCounty,selectWard,selectAge;
    private TextView CloseFrame;
    private String County,Age;
    private FloatingActionButton addCandidate;
    private CandidateAdapter adapter;
    private RecyclerView mRecyclerView;
    private FrameLayout frameLayoutPass;
    private LinearLayout linearLayoutDetails;
    private Button ChangePref,ConfirmPref;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircleImageView selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        mRecyclerView = findViewById(R.id.recycler_selection);
        frameLayoutPass = findViewById(R.id.Selection_pass);
        County = getIntent().getStringExtra("County");
        Age = getIntent().getStringExtra("Age");
        ConfirmPref = findViewById(R.id.confirm_candidate);
        ChangePref = findViewById(R.id.change_preference);
        toPref = findViewById(R.id.BackToPref);
        selectName = findViewById(R.id.Select_name);
        selectedImage = findViewById(R.id.Select_image);
        selectSalary = findViewById(R.id.Select_salary);
        selectCounty = findViewById(R.id.Select_county);
        selectWard = findViewById(R.id.Select_ward);
        CloseFrame = findViewById(R.id.Close_frame);
        linearLayoutDetails = findViewById(R.id.Deal_layout);
        selectAge  = findViewById(R.id.Select_age);


        CloseFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayoutPass.setVisibility(View.GONE);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeSelection);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchProduct();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });


        toPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
            }
        });

        ChangePref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
            }
        });

        ConfirmPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id2 != null){
                    Intent toUpdate = new Intent(getApplicationContext(), InfoActivity.class);
                    toUpdate.putExtra("ID",id2);
                    startActivity(toUpdate);
                }else {
                    ToastBack("Please select a candidate.");
                }

            }
        });

        FetchProduct();
    }





    private  String id2;
    private void FetchProduct() {

        Query query = CandidateRef.whereEqualTo("County",County)
                .whereEqualTo("Status","Available")
                .whereGreaterThanOrEqualTo("Age", Age)
                .limit(40);
        FirestoreRecyclerOptions<Candidates> transaction = new FirestoreRecyclerOptions.Builder<Candidates>()
                .setQuery(query, Candidates.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new CandidateAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(SelectionActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CandidateAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Candidates  candidates = documentSnapshot.toObject(Candidates.class);
                id2 = documentSnapshot.getId();
                frameLayoutPass.setVisibility(View.VISIBLE);
                selectName.setText(candidates.getCandidate_name());
                selectCounty.setText(candidates.getCounty());
                selectWard.setText(candidates.getWard());
                selectSalary.setText(candidates.getSalary());
                selectAge.setText(candidates.getAge());
                if (candidates.getProfile_image() != null){
                    Picasso.with(getApplicationContext()).load(candidates.getProfile_image()).placeholder(R.drawable.load)
                            .error(R.drawable.profile)
                            .into(selectedImage);
                }

                UpdateStatus(id2);

            }
        });

    }

    //-----Update Status ----
    private void UpdateStatus(String ID){

        HashMap<String, Object> update = new HashMap<>();
        update.put("Working_status", "selected");
        CandidateRef.document(ID).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {
                    ToastBack(task.getException().getMessage());
                }
            }
        });


    }



    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
//        View view = backToast.getView();
//
//        //Gets the actual oval background of the Toast then sets the colour filter
//        view.getBackground().setColorFilter(Color.parseColor("#0BF4DE"), PorterDuff.Mode.SRC_IN);
//
//        //Gets the TextView from the Toast so it can be editted
//        TextView text = view.findViewById(android.R.id.message);
//        text.setTextColor(Color.parseColor("#1C1B2B"));
        backToast.show();
    }

}